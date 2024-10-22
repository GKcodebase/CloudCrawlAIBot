/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The type Data collector application.
 */
@SpringBootApplication
@EnableScheduling
@EnableRetry
public class DataCollectorApplication {

    /**
     * The entry point of application.
     *
     * @param args
     *            the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DataCollectorApplication.class, args);
    }

}
