package org.openauto.webviewauto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconUtil {

    public static String getHost(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getScheme() + "://" + uri.getHost();
    }

    public static String getBase64Image(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(array, 0);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            input.close();
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getIconURL(String url) {
        try {
            Document doc = Jsoup.parse(new URL(url), 5000);
            Map<String, String> jsoupSelectors = new LinkedHashMap<>();
            jsoupSelectors.put("link[rel*='apple-touch-icon']", "href");
            jsoupSelectors.put("meta[property='og:image']", "content");
            jsoupSelectors.put("meta[content$='png']", "content");
            jsoupSelectors.put("link[rel='icon']", "href");
            jsoupSelectors.put("link[rel='shortcut icon']", "href");
            String imageURL = null;
            for (Map.Entry<String, String> entry : jsoupSelectors.entrySet()) {
                for (Element e : doc.select(entry.getKey())) {
                    imageURL = e.attr(entry.getValue());
                    if (isValidImageUrl(imageURL)) {
                        break;
                    }
                }
                if (isValidImageUrl(imageURL)) {
                    break;
                }
            }
            //Add domain if missing
            if(imageURL != null && !imageURL.startsWith("http")){
                if(imageURL.startsWith("//")){
                    imageURL = "https:" + imageURL;
                } else {
                    imageURL = getHost(url) + "/" + imageURL;
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
            return "nofavimg.png";
        }
    }

    private static boolean isValidImageUrl(String url){
        if(url == null){
            return false;
        }
        //we can't parse svg yet
        if(url.endsWith(".svg")){
            return false;
        }
        return true;
    }


}
