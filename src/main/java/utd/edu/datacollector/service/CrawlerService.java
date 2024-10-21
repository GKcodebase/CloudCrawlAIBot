package utd.edu.datacollector.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utd.edu.datacollector.dao.repository.CrawlerConfigurationRepository;
import utd.edu.datacollector.dao.repository.JobHistoryRepository;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.Data;
import utd.edu.datacollector.model.JobHistory;

import java.io.IOException;
import java.time.LocalDateTime;

import static utd.edu.datacollector.Enum.Status.FAILED;
import static utd.edu.datacollector.Enum.Status.SUCCESS;
import static utd.edu.datacollector.utility.AwsUtil.generateS3Key;
import static utd.edu.datacollector.utility.util.dataExtractor;

@Service
public class CrawlerService {

    @Autowired
    private CrawlerConfigurationRepository crawlerConfigurationRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    public CrawlerConfiguration createCrawler(CrawlerConfiguration config) {
        return crawlerConfigurationRepository.save(config);
    }

    public void crawlAndStoreToS3(Long configId) throws IOException {
        CrawlerConfiguration config = crawlerConfigurationRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid configuration ID"));



        JobHistory history = new JobHistory();
        history.setConfiguration(config);
        history.setStartTime(LocalDateTime.now());

        try {
            Document doc = Jsoup.connect(config.getUrl()).get();
            Data data  = dataExtractor(doc,config.getUrl());
            String s3Key = generateS3Key(config);
            s3Service.uploadToS3(data, s3Key);
            history.setStatus(SUCCESS);
        } catch (Exception e) {
            history.setStatus(FAILED);
        }
        // Schedule Task
        jobHistoryRepository.save(history);
    }
}
