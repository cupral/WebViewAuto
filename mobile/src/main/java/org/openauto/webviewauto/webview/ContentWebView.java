package org.openauto.webviewauto.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.openauto.webviewauto.BuildConfig;
import org.openauto.webviewauto.favorites.FavoriteManager;
import org.openauto.webviewauto.utils.HtmlUtils;

/**
 * Created by ljannace on 28/02/18.
 */

public class ContentWebView extends BaseWebView {

    private UrlInformation currentUrl = FAVORITES;
    public String originalAgentString;

    public ContentWebView(final Context context) {
        super(context);
    }

    public ContentWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentWebView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContentWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ContentWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void init(final WebSettings settings) {
        originalAgentString = settings.getUserAgentString();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        setWebChromeClient(chromeClient);
        setWebViewClient(client);
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);

        setUrl(currentUrl);
    }

    @Override
    protected Object createJavascriptInterface() {
        return new Object(){
            @JavascriptInterface
            public void clearLocalStorage(String s) {
                WebStorage.getInstance().deleteAllData();
            }

            @JavascriptInterface
            public void clearCookies(String s) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            }

            @JavascriptInterface
            public void showMenu(String s) {
                activity().menu().show();
            }

            @JavascriptInterface
            public void hideMenu(String s) {
                activity().menu().hide();
            }

            @JavascriptInterface
            public void openFavorite(String url, String desktopMode) {
                activity().setUrl(new UrlInformation(url, Boolean.parseBoolean(desktopMode)));
            }

            @JavascriptInterface
            public void loadVersion(String str) {
                post(() -> {
                    final String versionName = BuildConfig.VERSION_NAME;
                    evaluateJavascript("javascript:setVersion(\"" + "v" + versionName + "\");", null);
                });
            }

            @JavascriptInterface
            public void loadFavorites(String str) {
                post(() -> {
                    final String json = HtmlUtils.toJavascript(FavoriteManager.getInstance().favorites);
                    evaluateJavascript("javascript:parseFavorites(\"" + json + "\");", null);
                });
            }

            @JavascriptInterface
            public void resetFavorites(String str) {
                post(() -> {
                    FavoriteManager.getInstance().resetFavorites();
                    reload();
                });
            }

            @JavascriptInterface
            public void removeFavorite(String str) {
                post(() -> {
                    final FavoriteManager mgr = FavoriteManager.getInstance();
                    mgr.removeFavorite(mgr.getFavoriteById(str));
                    reload();
                });
            }
        };
    }

    private final WebViewClient client = new WebViewClient() {
        public void onPageFinished(WebView view, String url) {
            currentUrl = currentUrl.setUrl(url).setTitle(view.getTitle());
            activity().menu().setUrl(currentUrl);
        }
    };

    private final WebChromeClient chromeClient = new WebChromeClient() {
        private WebChromeClient.CustomViewCallback callback;

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if(activity().addFullscreenView(view)){
                this.callback = callback;
            }else{
                callback.onCustomViewHidden();
            }
        }

        @Override
        public void onHideCustomView() {
            if(activity().removeFullscreenView()) {
                callback.onCustomViewHidden();
            }
        }
    };

    private void setRenderMode(final UrlInformation urlInformation) {
        if (this.currentUrl != null && this.currentUrl.isDesktopMode() == urlInformation.isDesktopMode()) {
            return;
        }

        final WebSettings settings = getSettings();
        if (urlInformation.isDesktopMode()) {
            settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        } else {
            settings.setUserAgentString(originalAgentString);
            settings.setUseWideViewPort(false);
            settings.setLoadWithOverviewMode(false);
        }
    }

    public void setUrl(final UrlInformation urlInformation) {
        this.currentUrl = urlInformation;

        post(() -> {
            setRenderMode(currentUrl);
            loadUrl(currentUrl.getUrl());
        });
    }

    public void switchRenderMode() {
        this.currentUrl = new UrlInformation(currentUrl.getUrl(), !currentUrl.isDesktopMode()).setTitle(currentUrl.getTitle());

        post(() -> {
            setRenderMode(currentUrl);
            reload();
        });
    }

    public void back() {
        post(this::goBack);
    }

    public UrlInformation getCurrentUrl() {
        return currentUrl;
    }

    public void submitForm(){
        post(() -> {
            evaluateJavascript("document.activeElement.form.submit();", null);
        });
    }

    public void type(final String input){
        final String enteredText = HtmlUtils.toJavascript(input);
        final StringBuilder script = new StringBuilder();
        script.append("var wwaevent = new Event('change'); ");
        script.append("if(document.activeElement.isContentEditable){document.activeElement.innerText = \"").append(enteredText).append("\";}");
        script.append("else {document.activeElement.value = \"").append(enteredText).append("\";} document.activeElement.dispatchEvent(wwaevent);");

        post(() -> {
            evaluateJavascript(script.toString(), null);
        });
    }

    public void refresh() {
        post(this::reload);
    }
}
