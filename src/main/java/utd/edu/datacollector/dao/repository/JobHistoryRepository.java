/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import utd.edu.datacollector.model.JobHistory;

/**
 * The interface Job history repository.
 */
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {
}
