package utd.edu.datacollector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import utd.edu.datacollector.exception.CrawlerException;
import utd.edu.datacollector.exception.ErrorConfig;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.JobHistory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utd.edu.datacollector.Enum.Status.FAILED;
import static utd.edu.datacollector.constants.ErrorConstants.*;

@Service
public class ErrorHandlingService {
    @Autowired
    private ErrorConfig errorConfig;

    @Autowired
    private SchedulerService schedulerService;

    private final Map<String, RetryTemplate> retryTemplates = new ConcurrentHashMap<>();

    public void handleError(Exception e, CrawlerConfiguration config, JobHistory audit) {
        String errorCode = determineErrorCode(e);
        ErrorConfig.ErrorDefinition errorDef = errorConfig.getDefinitions().get(errorCode);

        if (errorDef == null) {
            handleUnknownError(e, config, audit);
            return;
        }
        audit.setStatus(FAILED);
        audit.setMessage(errorDef.getMessage() + ": " + e.getMessage());

        if (errorDef.isRetryable() && shouldRetry(errorCode, config)) {
            handleRetryableError(errorDef, config, e);
        } else {
            handleNonRetryableError(errorDef, config, e);
        }

    }
    private String determineErrorCode(Exception e) {
        if (e instanceof CrawlerException) {
            return ((CrawlerException) e).getErrorCode();
        }

        // Map common exceptions to error codes
        if (e instanceof java.net.ConnectException) {
            return CONNECTION_ERROR;
        } else if (e instanceof java.net.SocketTimeoutException) {
            return TIMEOUT_ERROR;
        } else if (e instanceof javax.net.ssl.SSLException) {
            return SSL_ERROR;
        } else if (e instanceof java.io.IOException) {
            return IO_ERROR;
        } else if(e instanceof CrawlerException)
            return DATA_NOT_FOUND;

        return UNKNOWN_ERROR;
    }
    private void handleUnknownError(Exception e, CrawlerConfiguration config, JobHistory audit) {
        audit.setStatus(FAILED);
        audit.setMessage("An unexpected error occurred: " + e.getMessage());
    }

    private boolean shouldRetry(String errorCode, CrawlerConfiguration config) {
        ErrorConfig.ErrorDefinition errorDef = errorConfig.getDefinitions().get(errorCode);
        return errorDef != null &&
                errorDef.isRetryable() &&
                errorDef.getMaxRetries() > 0;
    }

    private void handleRetryableError(ErrorConfig.ErrorDefinition errorDef,
                                      CrawlerConfiguration config,
                                      Exception e) {
        RetryTemplate retryTemplate = getOrCreateRetryTemplate(errorDef);

        try {
            retryTemplate.execute(context -> {
                schedulerService.updateTask(config);
                return null;
            });
        } catch (Exception retryException) {
            handleNonRetryableError(errorDef, config, retryException);
        }
    }

    private RetryTemplate getOrCreateRetryTemplate(ErrorConfig.ErrorDefinition errorDef) {
        return retryTemplates.computeIfAbsent(errorDef.getMessage(), k -> {
            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(errorDef.getMaxRetries());

            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(errorDef.getRetryDelayMs());
            backOffPolicy.setMultiplier(2.0);
            backOffPolicy.setMaxInterval(30000L);

            return RetryTemplate.builder()
                    .customPolicy(retryPolicy)
                    .customBackoff(backOffPolicy)
                    .build();
        });
    }

    private void handleNonRetryableError(ErrorConfig.ErrorDefinition errorDef,
                                         CrawlerConfiguration config,
                                         Exception e) {

        switch (errorDef.getAction()) {
            case STOP:
                stopCrawler(config);
                break;
            case CONTINUE:
                break;
            default:
        }
    }

    private void stopCrawler(CrawlerConfiguration config) {
        schedulerService.cancelTask(config.getId());
    }

}
