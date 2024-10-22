/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.service;

import static utd.edu.datacollector.Enum.Status.FAILED;
import static utd.edu.datacollector.constants.ErrorConstants.*;
import static utd.edu.datacollector.constants.Logging.CRAWLER_EXCEPTION;
import static utd.edu.datacollector.constants.Logging.CRAWLER_EXCEPTION_RETRY_FAILURE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import utd.edu.datacollector.exception.CrawlerException;
import utd.edu.datacollector.exception.ErrorConfig;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.JobHistory;

/**
 * The Error handling service - handle all exception.
 */
@Service
public class ErrorHandlingService {
    /**
     * The Error config.
     */
    @Autowired
    private ErrorConfig errorConfig;

    /**
     * The Scheduler service.
     */
    @Autowired
    private SchedulerService schedulerService;

    /**
     * The Retry templates.
     */
    private final Map<String, RetryTemplate> retryTemplates = new ConcurrentHashMap<>();
    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingService.class);

    /**
     * Handle error.
     *
     * Handle exception - check and retry task based on error.
     *
     * @param e
     *            the e
     * @param config
     *            the config
     * @param audit
     *            the audit
     */
    public void handleError(Exception e, CrawlerConfiguration config, JobHistory audit) {
        String errorCode = determineErrorCode(e);
        ErrorConfig.ErrorDefinition errorDef = errorConfig.getDefinitions().get(errorCode);

        if (errorDef == null) {
            handleUnknownError(e, config, audit);
            return;
        }
        logger.error(CRAWLER_EXCEPTION, errorCode, config.getId());
        audit.setStatus(FAILED);
        audit.setMessage(errorDef.getMessage() + ": " + e.getMessage());

        if (errorDef.isRetryable() && shouldRetry(errorCode, config)) {
            handleRetryableError(errorDef, config, e);
        } else {
            handleNonRetryableError(errorDef, config, e);
        }

    }

    /**
     * Determine error code string.
     *
     * @param e
     *            the e
     *
     * @return the string
     */
    private String determineErrorCode(Exception e) {
        if (e instanceof CrawlerException) {
            return ((CrawlerException) e).getErrorCode();
        }

        // Map common exceptions to error codes, mentioned in configs
        if (e instanceof java.net.ConnectException) {
            return CONNECTION_ERROR;
        } else if (e instanceof java.net.SocketTimeoutException) {
            return TIMEOUT_ERROR;
        } else if (e instanceof javax.net.ssl.SSLException) {
            return SSL_ERROR;
        } else if (e instanceof java.io.IOException) {
            return IO_ERROR;
        } else if (e instanceof CrawlerException)
            return DATA_NOT_FOUND;

        return UNKNOWN_ERROR;
    }

    /**
     * Handle unknown error. Unknown error will log and ignore. Update job status to FAILED
     *
     * @param e
     *            the e
     * @param config
     *            the config
     * @param audit
     *            the audit
     */
    private void handleUnknownError(Exception e, CrawlerConfiguration config, JobHistory audit) {
        audit.setStatus(FAILED);
        audit.setMessage("An unexpected error occurred: " + e.getMessage());
    }

    /**
     * Should retry boolean. check retry based on config
     *
     * @param errorCode
     *            the error code
     * @param config
     *            the config
     *
     * @return the boolean
     */
    private boolean shouldRetry(String errorCode, CrawlerConfiguration config) {
        ErrorConfig.ErrorDefinition errorDef = errorConfig.getDefinitions().get(errorCode);
        return errorDef != null && errorDef.isRetryable() && errorDef.getMaxRetries() > 0;
    }

    /**
     * Handle retryable error. Recreate task for retry able error.
     *
     * @param errorDef
     *            the error def
     * @param config
     *            the config
     * @param e
     *            the e
     */
    private void handleRetryableError(ErrorConfig.ErrorDefinition errorDef, CrawlerConfiguration config, Exception e) {
        RetryTemplate retryTemplate = getOrCreateRetryTemplate(errorDef);

        try {
            retryTemplate.execute(context -> {
                schedulerService.updateTask(config);
                return null;
            });
        } catch (Exception retryException) {
            logger.error(CRAWLER_EXCEPTION_RETRY_FAILURE, config.getId(), retryException);
            handleNonRetryableError(errorDef, config, retryException);
        }
    }

    /**
     * Gets or create retry template.
     *
     * @param errorDef
     *            the error def
     *
     * @return the or create retry template
     */
    private RetryTemplate getOrCreateRetryTemplate(ErrorConfig.ErrorDefinition errorDef) {
        return retryTemplates.computeIfAbsent(errorDef.getMessage(), k -> {
            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(errorDef.getMaxRetries());

            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(errorDef.getRetryDelayMs());
            backOffPolicy.setMultiplier(2.0);
            backOffPolicy.setMaxInterval(30000L);

            return RetryTemplate.builder().customPolicy(retryPolicy).customBackoff(backOffPolicy).build();
        });
    }

    /**
     * Handle non retryable error. Stop crawler for non retry errors
     *
     * @param errorDef
     *            the error def
     * @param config
     *            the config
     * @param e
     *            the e
     */
    private void handleNonRetryableError(ErrorConfig.ErrorDefinition errorDef, CrawlerConfiguration config,
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

    /**
     * Stop crawler. stopping crawler for unknown errors.
     *
     * @param config
     *            the config
     */
    private void stopCrawler(CrawlerConfiguration config) {
        schedulerService.cancelTask(config.getId());
    }

}
