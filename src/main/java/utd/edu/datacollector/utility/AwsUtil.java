package utd.edu.datacollector.utility;

import utd.edu.datacollector.model.CrawlerConfiguration;

import java.time.LocalDateTime;

public class AwsUtil {
    public static String generateS3Key(CrawlerConfiguration config) {
        return "crawls/" + config.getId() + "/" + LocalDateTime.now() + ".json";
    }
}
