/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utd.edu.datacollector.model.CrawlerConfiguration;

/**
 * The interface Crawler configuration repository.
 */
@Repository
public interface CrawlerConfigurationRepository extends JpaRepository<CrawlerConfiguration, Long> {
}
