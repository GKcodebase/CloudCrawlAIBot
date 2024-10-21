package utd.edu.datacollector.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.service.CrawlerService;

@RestController
@RequestMapping("/api/v1/crawlers")

public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping
    public ResponseEntity<?> createCrawlerConfiguration(@RequestBody CrawlerConfiguration request){
        CrawlerConfiguration createdConfig =  new CrawlerConfiguration();
        try{
            createdConfig = crawlerService.createCrawler(request);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e);
        }
        return ResponseEntity.ok(createdConfig);
    }

}
