package utd.edu.datacollector.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utd.edu.datacollector.dao.repository.CrawlerConfigurationRepository;
import utd.edu.datacollector.model.CrawlerConfiguration;

import java.io.IOException;

@Service
public class CrawlerService {

    @Autowired
    CrawlerConfigurationRepository crawlerConfigurationRepository;

    public CrawlerConfiguration createCrawler(CrawlerConfiguration config) {
        return crawlerConfigurationRepository.save(config);
    }

    public String crawl(Long configId) throws IOException {
        CrawlerConfiguration config = crawlerConfigurationRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid configuration ID"));

        Document doc = Jsoup.connect(config.getUrl()).get();
        Elements elements = doc.select(config.getTags());
        return elements.text();
    }
}
