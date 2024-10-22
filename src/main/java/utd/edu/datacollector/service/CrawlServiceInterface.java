package utd.edu.datacollector.service;

import utd.edu.datacollector.model.CrawlerConfiguration;

public interface CrawlServiceInterface {

    CrawlerConfiguration createCrawler(CrawlerConfiguration config) throws Exception;
    void crawlAndStoreToS3(Long configId);
}
