package utd.edu.datacollector.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utd.edu.datacollector.model.JobHistory;

public interface JobHistoryRepository  extends JpaRepository<JobHistory, Long> {
}
