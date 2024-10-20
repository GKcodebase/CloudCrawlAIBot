package utd.edu.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private Long id;
    private Long crawlerId;
    private String content;
    private String timestamp;
}
