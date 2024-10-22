/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.controller.v1;

import static utd.edu.datacollector.constants.Logging.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.service.CrawlServiceInterface;

/**
 * The Crawler controller - controller class.
 */
@RestController
@RequestMapping("/api/v1/crawlers")

public class CrawlerController {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    /**
     * The Crawler service.
     */
    private final CrawlServiceInterface crawlerService;

    /**
     * Instantiates a new Crawler controller.
     *
     * @param crawlerService
     *            the crawler service
     */
    @Autowired
    public CrawlerController(CrawlServiceInterface crawlerService) {
        this.crawlerService = crawlerService;
    }

    /**
     * Create crawler configuration response entity. Request :: CrawlerConfiguration Response :: 200 OK -> Success 500
     * Internal Server Exception
     *
     * @param request
     *            the request
     *
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<?> createCrawlerConfiguration(@RequestBody CrawlerConfiguration request) {
        logger.info(CRAWLER_REQUEST);
        CrawlerConfiguration createdConfig = new CrawlerConfiguration();
        try {
            createdConfig = crawlerService.createCrawler(request);
        } catch (Exception e) {
            logger.error(CRAWLER_ERROR, request.getId(), e.getMessage());
            return ResponseEntity.internalServerError().body(e);
        }
        logger.info(CRAWLER_REQUEST_COMPLETION, createdConfig.getId());
        return ResponseEntity.ok(createdConfig);
    }

}
