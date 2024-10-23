/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.constants;

/**
 * The Logging - Logging messages used in application.
 */
public class Logging {
    public static final String CRAWLER_REQUEST = "Crawler configuring request received";
    public static final String CRAWLER_ERROR = "Exception while creating crawler config for id {} :: {}";
    public static final String INVALID_REQUEST = "Invalid request received";

    public static final String CRAWLER_REQUEST_COMPLETION = "Crawler request completed : {}";
    public static final String SCHEDULER_CREATION = "Created scheduler job for id : {}";
    public static final String SCHEDULER_DELETION = "Created scheduler job for id : {}";
    public static final String SCHEDULER_CREATION_EXCEPTION = "Exception while Creating scheduler job for id : {} : {}";
    public static final String CRAWLER_EXCEPTION = "Crawler error occurred: {} for config: {}";
    public static final String CRAWLER_EXCEPTION_RETRY_FAILURE = "All retry attempts failed for config: {}";

}
