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
            ActivityAccessHelper.getInstance().getFavoriteManager().addFavorite(new FavoriteEnt("MENU_FAVORITES_" + title, title, url, false));
            ActivityAccessHelper.getInstance().getFavoriteManager().persistFavorites();
        }
    }

    @JavascriptInterface
    public void goBack(String str) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            WebView webView = (WebView)activity.findViewById(R.id.webview_component);
            webView.post(webView::goBack);
        }
    }

    @JavascriptInterface
    public void switchRenderMode(String s) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            WebView wv = (WebView)((WebViewAutoActivity)context).findViewById(R.id.webview_component);
            wv.post(() -> {
                if(activity.renderMode == WebViewAutoActivity.BrowserRenderMode.DESKTOP_MODE){
                    activity.renderMode = WebViewAutoActivity.BrowserRenderMode.MOBILE_MODE;
                    activity.setMobileMode();
                } else {
                    activity.renderMode = WebViewAutoActivity.BrowserRenderMode.DESKTOP_MODE;
                    activity.setDesktopMode();
                }
                wv.post(wv::reload);
            });
        }
    }

}