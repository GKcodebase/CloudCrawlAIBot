package utd.edu.datacollector.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import utd.edu.datacollector.dao.repository.CrawlerConfigurationRepository;
import utd.edu.datacollector.dao.repository.JobHistoryRepository;
import utd.edu.datacollector.model.CrawlerConfiguration;
import utd.edu.datacollector.model.JobHistory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import static utd.edu.datacollector.Enum.Status.*;

@Service
public class SchedulerService {

    private final CrawlServiceInterface crawlerService;

    @Autowired
    public SchedulerService(CrawlServiceInterface crawlerService) {
        this.crawlerService = crawlerService;
    }

    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    @Autowired
    private CrawlerConfigurationRepository configRepository;


    @Autowired
    private TaskScheduler taskScheduler;


    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    public void initializeScheduler() {
        Iterable<CrawlerConfiguration> configurations = configRepository.findAll();
        for (CrawlerConfiguration config : configurations) {
            scheduleTask(config);
        }
    }

    public void scheduleTask(CrawlerConfiguration config) {
        Runnable task = () -> {
            JobHistory audit = new JobHistory();
            audit.setConfiguration(config);
            audit.setStartTime(LocalDateTime.now());
            audit.setStatus(STARTED);
            jobHistoryRepository.save(audit);
            try {
                crawlerService.crawlAndStoreToS3(config.getId());
                audit.setStatus(COMPLETED);
            } catch (Exception e) {
                audit.setStatus(FAILED);
                audit.setMessage(e.getMessage());
            } finally {
                audit.setEndTime(LocalDateTime.now());
                jobHistoryRepository.save(audit);
            }
        };

        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        CronTrigger trigger = new CronTrigger(config.getCronExpression(), timeZone);

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, trigger);
        scheduledTasks.put(config.getId(), scheduledTask);
    }

    public void addNewConfig(CrawlerConfiguration config){
        scheduleTask(config);
    }
}
