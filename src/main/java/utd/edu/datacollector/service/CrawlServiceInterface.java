package utd.edu.datacollector.service;

import utd.edu.datacollector.model.CrawlerConfiguration;

public interface CrawlServiceInterface {

    CrawlerConfiguration createCrawler(CrawlerConfiguration config);
    void crawlAndStoreToS3(Long configId);
}
