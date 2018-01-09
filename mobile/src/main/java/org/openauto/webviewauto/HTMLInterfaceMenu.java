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
            ((WebViewAutoActivity)context).toggleKeyboard();
        }
    }


}