package org.openauto.webviewauto.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconUtil {

    public static String getIconURL(String url) {
        try {
            Document doc = Jsoup.parse(new URL(url), 5000);
            Map<String, String> jsoupSelectors = new LinkedHashMap<>();
            jsoupSelectors.put("link[rel*='apple-touch-icon']", "href");
            jsoupSelectors.put("meta[property='og:image']", "content");
            jsoupSelectors.put("meta[content$='png']", "content");
            jsoupSelectors.put("link[rel='icon']", "href");
            String imageURL = null;
            for (Map.Entry<String, String> entry : jsoupSelectors.entrySet()) {
                for (Element e : doc.select(entry.getKey())) {
                    imageURL = e.attr(entry.getValue());
                    if (imageURL != null) {
                        break;
                    }
                }
                if (imageURL != null) {
                    break;
                }
            }
            //Add domain if missing
            if(imageURL != null && !imageURL.startsWith("http")){
                if(imageURL.startsWith("//")){
                    //CDN
                    imageURL = "https://" + imageURL;
                } else if(url.endsWith("/") && imageURL.startsWith("/")){
                    imageURL = url + imageURL.substring(1);
                } else if(url.endsWith("/") || imageURL.startsWith("/")){
                    imageURL = url + imageURL;
                } else {
                    imageURL = url + "/" + imageURL;
                }
            }
            if(imageURL == null){
                Log.i("No Icon found for", url);
            } else {
                Log.i("IconUtil Icon found", imageURL);
            }
            return imageURL;
        }catch(Exception e){
            Log.i("Exception", e.getMessage());
            return "";
        }
    }


}
