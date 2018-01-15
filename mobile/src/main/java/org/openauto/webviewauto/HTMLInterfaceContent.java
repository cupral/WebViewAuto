package org.openauto.webviewauto;

import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;

public class HTMLInterfaceContent {

    private Context context;

    HTMLInterfaceContent(Context context) {
        this.context = context;
    }


    @JavascriptInterface
    public void showMenu(String s) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            WebView wv = (WebView)((WebViewAutoActivity)context).findViewById(R.id.webview_component);
            wv.post(() -> {
                activity.findViewById(R.id.html5_menu).setVisibility(View.VISIBLE);
            });
        }
    }

    @JavascriptInterface
    public void hideMenu(String s) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            WebView wv = (WebView)((WebViewAutoActivity)context).findViewById(R.id.webview_component);
            wv.post(() -> {
                activity.findViewById(R.id.html5_menu).setVisibility(View.GONE);
            });
        }
    }

    @JavascriptInterface
    public void openFavorite(String url, String desktopMode) {
        if (context instanceof WebViewAutoActivity){
            WebView wv = (WebView)((WebViewAutoActivity)context).findViewById(R.id.webview_component);
            wv.post(() -> {
                ((WebViewAutoActivity)context).changeURL(url, Boolean.parseBoolean(desktopMode));
            });
        }
    }

    @JavascriptInterface
    public void loadFavorites(String str) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = ((WebViewAutoActivity)context);
            WebView wv = (WebView)activity.findViewById(R.id.webview_component);
            wv.post(() -> {
                Gson g = new Gson();
                String json = StringEscapeUtils.escapeEcmaScript(g.toJson(activity.favoriteManager.favorites));
                wv.evaluateJavascript("javascript:parseFavorites(\"" + json + "\");", null);
            });
        }
    }

    @JavascriptInterface
    public void resetFavorites(String str) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = ((WebViewAutoActivity)context);
            WebView wv = (WebView)activity.findViewById(R.id.webview_component);
            wv.post(() -> {
                activity.favoriteManager.resetFavorites();
            });
        }
    }


}
