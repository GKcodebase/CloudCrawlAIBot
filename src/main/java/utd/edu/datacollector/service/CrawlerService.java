/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.service;

import static utd.edu.datacollector.Enum.Status.FAILED;
import static utd.edu.datacollector.Enum.Status.SUCCESS;
import static utd.edu.datacollector.constants.ErrorConstants.DATA_NOT_FOUND;
import static utd.edu.datacollector.exception.CrawlerException.Severity.WARNING;
import static utd.edu.datacollector.utility.AwsUtil.generateS3Key;
import static utd.edu.datacollector.utility.DataExtractor.dataExtractor;

import java.io.IOException;
import java.time.LocalDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utd.edu.datacollector.dao.repository.CrawlerConfigurationRepository;
import utd.edu.datacollector.dao.repository.JobHistoryRepository;
import utd.edu.datacollector.exception.CrawlerException;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.JobHistory;

/**
 * The Crawler service - service layer for all crawler related task.
 */
@Service
public class CrawlerService implements CrawlServiceInterface {

    /**
     * The Crawler configuration repository.
     */
    @Autowired
    private CrawlerConfigurationRepository crawlerConfigurationRepository;

    /**
     * The S3 service.
     */
    @Autowired
    private S3Service s3Service;

    /**
     * The Job history repository.
     */
    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    /**
     * The Scheduler service.
     */
    @Autowired
    private SchedulerService schedulerService;

    /**
     * The Error handling service.
     */
    @Autowired
    private ErrorHandlingService errorHandlingService;

    /**
     * Create crawler configuration. Add config to scheduler Thread pool. Add config to DB Returns modified request with
     * config id
     *
     * @param config
     *            the config
     *
     * @return the crawler configuration
     *
     * @throws Exception
     *             the exception
     */
    // TODO - Handle db commit failure and scheduler additon
    @Override
    public CrawlerConfiguration createCrawler(CrawlerConfiguration config) throws Exception {
        schedulerService.addNewConfig(config);
        return crawlerConfigurationRepository.save(config);
    }

    /**
     * Crawl and store to s3. 1.Crawl the given url 2.Update job audit 3.Upload data s3 data lake
     *
     * @param configId
     *            the config id
     *
     * @throws CrawlerException
     *             the crawler exception
     */
    @Override
    public void crawlAndStoreToS3(Long configId) throws CrawlerException, IOException {
        CrawlerConfiguration config = crawlerConfigurationRepository.findById(configId)
                .orElseThrow(() -> new CrawlerException(DATA_NOT_FOUND, configId, WARNING, false));

        JobHistory history = new JobHistory();
        history.setConfiguration(config);
        history.setStartTime(LocalDateTime.now());
        try {
            // Data Crawler
            Document doc = Jsoup.connect(config.getUrl()).get();
            // DataLake data upload
            s3Service.uploadToS3(dataExtractor(doc, config.getUrl()), generateS3Key(config));
            history.setStatus(SUCCESS);
        } catch (Exception e) {
            history.setStatus(FAILED);
            errorHandlingService.handleError(e, config, history);
            throw e;
        }
        jobHistoryRepository.save(history);
    }
}
