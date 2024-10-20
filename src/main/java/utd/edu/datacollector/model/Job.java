package utd.edu.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utd.edu.datacollector.Enum.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private Long id;
    private Long crawlerId;
    private Status status;
    private String resultFilePath;
}
