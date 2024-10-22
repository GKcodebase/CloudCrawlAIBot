/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.utility;

import java.time.LocalDateTime;

import utd.edu.datacollector.model.CrawlerConfiguration;

/**
 * The type Aws util.
 */
public class AwsUtil {
    /**
     * Generate s3 key string. s3 File name
     *
     * @param config
     *            the config
     *
     * @return the string
     */
    public static String generateS3Key(CrawlerConfiguration config) {
        return "crawls/" + config.getId() + "/" + LocalDateTime.now() + ".json";
    }
}
