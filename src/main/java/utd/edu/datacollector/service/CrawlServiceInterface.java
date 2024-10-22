/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.service;

import java.io.IOException;

import utd.edu.datacollector.model.CrawlerConfiguration;

/**
 * The interface Crawl service interface.
 */
public interface CrawlServiceInterface {

    /**
     * Create crawler configuration.
     *
     * @param config
     *            the config
     *
     * @return the crawler configuration
     *
     * @throws Exception
     *             the exception
     */
    CrawlerConfiguration createCrawler(CrawlerConfiguration config) throws Exception;

    /**
     * Crawl and store to s3.
     *
     * @param configId
     *            the config id
     *
     * @throws IOException
     *             the io exception
     */
    void crawlAndStoreToS3(Long configId) throws IOException;
}
