package org.openauto.webviewauto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconUtil {

    public static String getBase64Image(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(array, 0);
    }

    /**
     * from : https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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
