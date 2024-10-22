package utd.edu.datacollector.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.service.CrawlServiceInterface;

import static utd.edu.datacollector.constants.Logging.*;

@RestController
@RequestMapping("/api/v1/crawlers")

public class CrawlerController {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    private final CrawlServiceInterface crawlerService;

    @Autowired
    public CrawlerController(CrawlServiceInterface crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping
    public ResponseEntity<?> createCrawlerConfiguration(@RequestBody CrawlerConfiguration request){
        logger.info(CRAWLER_REQUEST);
        CrawlerConfiguration createdConfig =  new CrawlerConfiguration();
        try{
            createdConfig = crawlerService.createCrawler(request);
        }catch (Exception e){
            logger.error(CRAWLER_ERROR,request.getId(),e.getMessage());
            return ResponseEntity.internalServerError().body(e);
        }
        logger.info(CRAWLER_REQUEST_COMPLETION,createdConfig.getId() );
        return ResponseEntity.ok(createdConfig);
    }

}
