package org.openauto.webviewauto;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class HTMLInterfaceKeyboard {

    private Context context;

    HTMLInterfaceKeyboard(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void submitInput(String str) {
        if (context instanceof WebViewAutoActivity){
            ((WebViewAutoActivity)context).keyboardInputCallback(str);
        }
    }

}