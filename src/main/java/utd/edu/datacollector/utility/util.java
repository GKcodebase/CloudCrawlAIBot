package utd.edu.datacollector.utility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utd.edu.datacollector.model.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class util {
    public static Data dataExtractor(Document doc, String url){
            Data data =  new Data(url,doc.title(),extractText(doc),extractMetadata(doc),extractLinks(doc),extractImages(doc));
            return data;
    }

    private static String extractText(Document doc) {
        return doc.body().text();
    }

    private static Map<String, String> extractMetadata(Document doc) {
        Map<String, String> metadata = new HashMap<>();

        Elements metaTags = doc.select("meta");
        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            String content = metaTag.attr("content");
            if (!name.isEmpty() && !content.isEmpty()) {
                metadata.put(name, content);
            }
        }

        Elements ogTags = doc.select("meta[property^=og:]");
        for (Element ogTag : ogTags) {
            String property = ogTag.attr("property");
            String content = ogTag.attr("content");
            metadata.put(property, content);
        }

        return metadata;
    }

    private static List<String> extractLinks(Document doc) {
        List<String> links = new ArrayList<>();
        Elements linkElements = doc.select("a[href]");
        for (Element link : linkElements) {
            links.add(link.attr("abs:href"));
        }
        return links;
    }

    private static List<Map<String, String>> extractImages(Document doc) {
        List<Map<String, String>> images = new ArrayList<>();
        Elements imageElements = doc.select("img");
        for (Element img : imageElements) {
            Map<String, String> imageData = new HashMap<>();
            imageData.put("src", img.attr("abs:src"));
            imageData.put("alt", img.attr("alt"));
            imageData.put("title", img.attr("title"));
            images.add(imageData);
        }
        return images;
    }
}
