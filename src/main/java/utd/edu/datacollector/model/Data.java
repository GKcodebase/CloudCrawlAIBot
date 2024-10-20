package utd.edu.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private String url;
    private String title;
    private String text;
    private Map<String, String> metadata;
    private List<String> links;
    private List<Map<String, String>> images;
}
