package org.openauto.webviewauto;

import org.openauto.webviewauto.favorites.FavoriteManager;

public final class ActivityAccessHelper {

    private FavoriteManager favoriteManager;
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

    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    public void setFavoriteManager(FavoriteManager favoriteManager) {
        this.favoriteManager = favoriteManager;
    }

    public WebViewAutoActivity getActivity() {
        return activity;
    }

    public void setActivity(WebViewAutoActivity activity) {
        this.activity = activity;
    }
}