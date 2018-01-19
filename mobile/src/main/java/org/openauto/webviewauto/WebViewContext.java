package org.openauto.webviewauto;

import android.app.Application;
import android.content.Context;

public class WebViewContext extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        WebViewContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return WebViewContext.context;
    }

}