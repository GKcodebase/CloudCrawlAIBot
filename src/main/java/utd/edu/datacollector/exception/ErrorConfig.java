/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.exception;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * The Error config. bean used to define error and retry.
 */
@Configuration
@ConfigurationProperties(prefix = "crawler.error")
@Data
public class ErrorConfig {

    /**
     * The Definitions.
     */
    private Map<String, ErrorDefinition> definitions;

    /**
     * The type Error definition.
     */
    @Data
    public static class ErrorDefinition {
        /**
         * The Message.
         */
        private String message;
        /**
         * The Severity.
         */
        private String severity;
        /**
         * The Retryable.
         */
        private boolean retryable;
        /**
         * The Max retries.
         */
        private int maxRetries;
        /**
         * The Retry delay ms.
         */
        private long retryDelayMs;
        /**
         * The Action.
         */
        private String action;
    }
}
