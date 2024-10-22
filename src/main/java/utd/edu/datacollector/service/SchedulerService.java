package utd.edu.datacollector.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static utd.edu.datacollector.constants.Logging.*;

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
    private ErrorHandlingService errorHandlingService;


    @Autowired
    private TaskScheduler taskScheduler;


    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);


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
                logger.info(SCHEDULER_CREATION,config.getId());
            } catch (Exception e) {
                audit.setStatus(FAILED);
                audit.setMessage(e.getMessage());
                logger.info(SCHEDULER_CREATION_EXCEPTION,config.getId(),e.getMessage());
                errorHandlingService.handleError(e,config,audit);
                throw e;
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

    public void addNewConfig(CrawlerConfiguration config) throws Exception{
        scheduleTask(config);
    }

    public void cancelTask(Long configId) {
        logger.info(SCHEDULER_DELETION,configId);
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(configId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTasks.remove(configId);
        }
    }

    public void updateTask(CrawlerConfiguration config) {
        cancelTask(config.getId());
        scheduleTask(config);
    }
}
