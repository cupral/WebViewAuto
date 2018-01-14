package org.openauto.webviewauto;

import android.content.Context;
import android.webkit.JavascriptInterface;

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
    public void addToFavorites(String str) {
        if (context instanceof WebViewAutoActivity){
            //((WebViewAutoActivity)context).addToFavorites();
        }
    }

    @JavascriptInterface
    public void goBack(String str) {
        if (context instanceof WebViewAutoActivity){
            WebViewAutoActivity activity = (WebViewAutoActivity)context;
            int historySize = activity.urlHistory.size();
            int newIndex = historySize - 2;
            if(newIndex > 0 && newIndex < historySize){
                String newURL = activity.urlHistory.get(newIndex);
                activity.changeURL(newURL, false);
            }
        }
    }

}