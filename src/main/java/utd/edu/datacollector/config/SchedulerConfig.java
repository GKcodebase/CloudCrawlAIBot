package utd.edu.datacollector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * The Scheduler config.
 */
@Configuration
public class SchedulerConfig {
    /**
     * Task scheduler.
     * Create Threads for running scheduler.
     * Pool is fixed with 1o thread size
     * use "crawl-task-" prefix for debugging
     *
     * @return the task scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("crawl-task-");
        scheduler.initialize();
        return scheduler;
    }
}
