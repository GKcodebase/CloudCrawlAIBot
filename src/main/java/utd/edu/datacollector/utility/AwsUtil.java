package utd.edu.datacollector.utility;

import utd.edu.datacollector.model.CrawlerConfiguration;

import java.time.LocalDateTime;

/**
 * The type Aws util.
 */
public class AwsUtil {
    /**
     * Generate s3 key string.
     * s3 File name
     *
     * @param config the config
     * @return the string
     */
    public static String generateS3Key(CrawlerConfiguration config) {
        return "crawls/" + config.getId() + "/" + LocalDateTime.now() + ".json";
    }
}
