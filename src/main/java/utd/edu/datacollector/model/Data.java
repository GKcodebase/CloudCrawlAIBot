package utd.edu.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * The Data - bean for crawled data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    /**
     * The Url.
     */
    private String url;
    /**
     * The Title.
     */
    private String title;
    /**
     * The Text.
     */
    private String text;
    /**
     * The Metadata.
     */
    private Map<String, String> metadata;
    /**
     * The Links.
     */
    private List<String> links;
    /**
     * The Images.
     */
    private List<Map<String, String>> images;
}
