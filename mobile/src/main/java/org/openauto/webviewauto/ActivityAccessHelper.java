package org.openauto.webviewauto;

public final class ActivityAccessHelper {

    private WebViewAutoActivity activity;
    private static volatile ActivityAccessHelper instance = null;

    private ActivityAccessHelper() {}

    public static ActivityAccessHelper getInstance() {
        if (instance == null) {
            synchronized(ActivityAccessHelper.class) {
                if (instance == null) {
                    instance = new ActivityAccessHelper();
                }
            }
        }
        return instance;
    }

    public WebViewAutoActivity getActivity() {
        return activity;
    }

    public void setActivity(WebViewAutoActivity activity) {
        this.activity = activity;
    }
}