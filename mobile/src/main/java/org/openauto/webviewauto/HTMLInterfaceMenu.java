package org.openauto.webviewauto;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.openauto.webviewauto.favorites.FavoriteEnt;

public class HTMLInterfaceMenu {

    private Context context;

    HTMLInterfaceMenu(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void openMenu(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).openDrawer();
        }
    }

    @JavascriptInterface
    public void toggleKeyboard(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).toggleKeyboard(WebViewAutoActivity.BrowserInputMode.CONTENT_INPUT_MODE);
        }
    }

    @JavascriptInterface
    public void toggleURLKeyboard(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).toggleURLKeyboard(str);
        }
    }

    @JavascriptInterface
    public void submitForm(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).submitForm();
        }
    }

    @JavascriptInterface
    public void showFavorites(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).showFavorites();
        }
    }

    @JavascriptInterface
    public void addToFavorites(String title, String url) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).favoriteManager.addFavorite(new FavoriteEnt("MENU_FAVORITES_" + title, title, url, false));
            ((WebViewAutoActivity)context).favoriteManager.persistFavorites();
        }
    }

    @JavascriptInterface
    public void goBack(String str) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            WebView webView = (WebView)activity.findViewById(R.id.webview_component);
            webView.post(() -> {
                int historySize = activity.urlHistory.size();
                int newIndex = historySize - 1;
                if(newIndex > 0){
                    String newURL = activity.urlHistory.get(newIndex);
                    activity.changeURL(newURL, false);
                    activity.urlHistory.remove(activity.urlHistory.size()-1);
                }
            });
        }
    }

}