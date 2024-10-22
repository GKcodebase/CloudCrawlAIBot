/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The Crawler exception : bean for exception.
 */
@Getter
@Setter
@AllArgsConstructor
public class CrawlerException extends RuntimeException {
    /**
     * The Error code.
     */
    private final String errorCode;
    /**
     * The Config id.
     */
    private final Long configId;
    /**
     * The Severity.
     */
    private final Severity severity;
    /**
     * The Retryable.
     */
    private final boolean retryable;

    /**
     * The enum Severity.
     */
    public enum Severity {
        /**
         * Warning severity.
         */
        WARNING,
        /**
         * Error severity.
         */
        ERROR,
        /**
         * Critical severity.
         */
        CRITICAL
    }
}
