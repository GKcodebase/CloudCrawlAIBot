/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Crawler configuration entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "crawler_configurations")
public class CrawlerConfiguration {

    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The Url.
     */
    private String url;
    /**
     * The Cron expression.
     */
    private String cronExpression;
}
