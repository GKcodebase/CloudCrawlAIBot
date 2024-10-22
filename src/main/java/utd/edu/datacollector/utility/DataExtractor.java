package utd.edu.datacollector.utility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utd.edu.datacollector.model.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utd.edu.datacollector.constants.CssQuery.*;

/**
 * The Util. - Utility services for data extraction
 */
public class DataExtractor {
    /**
     * Data extractor data.
     *
     * @param doc the doc
     * @param url the url
     * @return the data
     */
    public static Data dataExtractor(Document doc, String url){
            Data data =  new Data(url,doc.title(),extractText(doc),extractMetadata(doc),extractLinks(doc),extractImages(doc));
            return data;
    }

    /**
     * Extract text string.
     *
     * @param doc the doc
     * @return the string
     */
    private static String extractText(Document doc) {
        return doc.body().text();
    }

    /**
     * Extract metadata map.
     *
     * @param doc the doc
     * @return the map
     */
    private static Map<String, String> extractMetadata(Document doc) {
        Map<String, String> metadata = new HashMap<>();

        Elements metaTags = doc.select(META);
        for (Element metaTag : metaTags) {
            String name = metaTag.attr(NAME);
            String content = metaTag.attr(CONTENT);
            if (!name.isEmpty() && !content.isEmpty()) {
                metadata.put(name, content);
            }
        }

        Elements ogTags = doc.select(META_TAG);
        for (Element ogTag : ogTags) {
            String property = ogTag.attr(PROPERTY);
            String content = ogTag.attr(CONTENT);
            metadata.put(property, content);
        }

        return metadata;
    }

    /**
     * Extract links list.
     *
     * @param doc the doc
     * @return the list
     */
    private static List<String> extractLinks(Document doc) {
        List<String> links = new ArrayList<>();
        Elements linkElements = doc.select(HREF_ATR_KEY);
        for (Element link : linkElements) {
            links.add(link.attr(HREF_ATR));
        }
        return links;
    }

    /**
     * Extract images list.
     *
     * @param doc the doc
     * @return the list
     */
    private static List<Map<String, String>> extractImages(Document doc) {
        List<Map<String, String>> images = new ArrayList<>();
        Elements imageElements = doc.select(IMAGE);
        for (Element img : imageElements) {
            Map<String, String> imageData = new HashMap<>();
            imageData.put(SRC, img.attr(SRC_ATR));
            imageData.put(ALT, img.attr(ALT));
            imageData.put(TITLE, img.attr(TITLE));
            images.add(imageData);
        }
        return images;
    }
}
