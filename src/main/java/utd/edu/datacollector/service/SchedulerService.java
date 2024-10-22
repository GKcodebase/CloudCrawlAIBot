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

/**
 * The Scheduler service.
 */
@Service
public class SchedulerService {

    /**
     * The Crawler service.
     */
    private final CrawlServiceInterface crawlerService;

    /**
     * Instantiates a new Scheduler service.
     *
     * @param crawlerService the crawler service
     */
    @Autowired
    public SchedulerService(CrawlServiceInterface crawlerService) {
        this.crawlerService = crawlerService;
    }

    /**
     * The Job history repository.
     */
    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    /**
     * The Config repository.
     */
    @Autowired
    private CrawlerConfigurationRepository configRepository;

    /**
     * The Error handling service.
     */
    @Autowired
    private ErrorHandlingService errorHandlingService;


    /**
     * The Task scheduler.
     */
    @Autowired
    private TaskScheduler taskScheduler;


    /**
     * The Scheduled tasks.
     */
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);


    /**
     * Initialize scheduler.
     * On Service start, get all existing config and create threads
     */
    @PostConstruct
    public void initializeScheduler() {
        Iterable<CrawlerConfiguration> configurations = configRepository.findAll();
        for (CrawlerConfiguration config : configurations) {
            scheduleTask(config);
        }
    }

    /**
     * Schedule task.
     * Create scheduler Threads based cron expression
     * Update job history in DB
     *
     * @param config the config
     */
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

    /**
     * Add new config.
     *
     * @param config the config
     * @throws Exception the exception
     */
    public void addNewConfig(CrawlerConfiguration config) throws Exception{
        scheduleTask(config);
    }

    /**
     * Cancel task.
     *
     * @param configId the config id
     */
    public void cancelTask(Long configId) {
        logger.info(SCHEDULER_DELETION,configId);
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(configId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTasks.remove(configId);
        }
    }

    /**
     * Update task.
     *
     * @param config the config
     */
    public void updateTask(CrawlerConfiguration config) {
        cancelTask(config.getId());
        scheduleTask(config);
    }
}
