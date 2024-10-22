package utd.edu.datacollector.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "crawler.error")
@Data
public class ErrorConfig {

    private Map<String, ErrorDefinition> definitions;

    @Data
    public static class ErrorDefinition {
        private String message;
        private String severity;
        private boolean retryable;
        private int maxRetries;
        private long retryDelayMs;
        private String action;
    }
}
