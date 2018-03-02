package org.openauto.webviewauto.webview;

/**
 * Created by ljannace on 02/03/18.
 */

public class UrlInformation {
    private final String url;
    private boolean desktopMode;
    private String title;

    public UrlInformation(final String url, final boolean desktopMode) {
        this.url = url;
        this.desktopMode = desktopMode;
    }

    public String getUrl() {
        return url;
    }

    public UrlInformation setUrl(final String url){
        return url.equals(this.url) ? this : new UrlInformation(url, desktopMode);
    }

    public UrlInformation setDesktopMode(final boolean desktopMode){
        return this.desktopMode == desktopMode ? this : new UrlInformation(url, desktopMode).setTitle(title);
    }

    public boolean isDesktopMode() {
        return desktopMode;
    }

    public UrlInformation setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

}
