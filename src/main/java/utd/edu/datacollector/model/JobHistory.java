/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utd.edu.datacollector.Enum.Status;

/**
 * The Job history entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_history")
public class JobHistory {
    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Configuration.
     */
    @ManyToOne
    @JoinColumn(name = "configuration_id", nullable = false)
    private CrawlerConfiguration configuration;

    /**
     * The Start time.
     */
    private LocalDateTime startTime;

    /**
     * The End time.
     */
    private LocalDateTime endTime;

    /**
     * The Status.
     */
    private Status status;

    /**
     * The Message.
     */
    private String message;
}
