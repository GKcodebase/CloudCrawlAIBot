package utd.edu.datacollector.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utd.edu.datacollector.model.CrawlerConfiguration;
@Repository
public interface CrawlerConfigurationRepository extends JpaRepository<CrawlerConfiguration, Long> {
}
