package utd.edu.datacollector.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utd.edu.datacollector.dao.repository.CrawlerConfigurationRepository;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.Data;

import java.io.IOException;

import static utd.edu.datacollector.utility.AwsUtil.generateS3Key;
import static utd.edu.datacollector.utility.util.dataExtractor;

@Service
public class CrawlerService {

    @Autowired
    CrawlerConfigurationRepository crawlerConfigurationRepository;

    @Autowired
    private S3Service s3Service;

    public CrawlerConfiguration createCrawler(CrawlerConfiguration config) {
        return crawlerConfigurationRepository.save(config);
    }

    public Data crawl(Long configId) throws IOException {
        CrawlerConfiguration config = crawlerConfigurationRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid configuration ID"));

        Document doc = Jsoup.connect(config.getUrl()).get();
        Data data  = dataExtractor(doc,config.getUrl());
        String s3Key = generateS3Key(config);
        s3Service.uploadToS3(data, s3Key);
        return data;
    }
}
