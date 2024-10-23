/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.controller.v1;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.service.CrawlServiceInterface;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Crawler controller test. - UT's for controller
 */
@SpringBootTest
class CrawlerControllerTest {

    @InjectMocks
    CrawlerController controller;

    @Mock
    CrawlServiceInterface crawlService;

    @Test
    public void successTest() throws Exception {
        CrawlerConfiguration configuration = new CrawlerConfiguration(123L,"url","cron");
        Mockito.when(crawlService.createCrawler(configuration)).thenReturn(configuration);
        assertEquals("200 OK",controller.createCrawlerConfiguration(configuration).getStatusCode());
    }

    @Test
    public void badRequestTest() throws Exception {
        CrawlerConfiguration configuration = new CrawlerConfiguration();
        Mockito.when(crawlService.createCrawler(configuration)).thenReturn(configuration);
        assertEquals(400,controller.createCrawlerConfiguration(configuration).getStatusCodeValue());


        configuration = new CrawlerConfiguration(123L,"url",null);
        assertEquals(400,controller.createCrawlerConfiguration(configuration).getStatusCodeValue());

        configuration = new CrawlerConfiguration(123L,null,"crom");
        assertEquals(400,controller.createCrawlerConfiguration(configuration).getStatusCodeValue());

    }

}