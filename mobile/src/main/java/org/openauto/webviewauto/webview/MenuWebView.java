package org.openauto.webviewauto.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import org.openauto.webviewauto.favorites.FavoriteEnt;
import org.openauto.webviewauto.favorites.FavoriteManager;
import org.openauto.webviewauto.utils.NetworkReaderTask;

/**
 * Created by ljannace on 28/02/18.
 */

public class MenuWebView extends BaseWebView {

    public MenuWebView(final Context context) {
        super(context);
    }

    public MenuWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuWebView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MenuWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MenuWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void init(final WebSettings settings) {
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        setWebChromeClient(new WebChromeClient());
        loadUrl("file:///android_asset/menu/menu.html");
    }

    @Override
    protected Object createJavascriptInterface() {
        return new Object() {
            @JavascriptInterface
            public void openMenu(String str) {
                activity().openDrawer();
            }

            @JavascriptInterface
            public void toggleKeyboard(String str) {
                activity().keyboard().showForInput();
            }

            @JavascriptInterface
            public void toggleURLKeyboard(String str) {
                activity().keyboard().showForUrl();
            }

            @JavascriptInterface
            public void submitForm(String str) {
                activity().content().submitForm();
            }

            @JavascriptInterface
            public void showFavorites(String str) {
                activity().setUrl(FAVORITES);
            }

            @JavascriptInterface
            public void addToFavorites(String title, String url) {
                //File favorites are forbidden
                if (url.startsWith("file:") && url.endsWith("favorites.html")) {
                    return;
                }

                final FavoriteEnt newFavorite = new FavoriteEnt("MENU_FAVORITES_" + title, title, url, false);
                FavoriteManager.getInstance().addFavorite(newFavorite);

                //Load icon
                new NetworkReaderTask(newFavorite, false).execute();

                FavoriteManager.getInstance().persistFavorites();
            }

            @JavascriptInterface
            public void goBack(String str) {
                activity().content().back();
            }

            @JavascriptInterface
            public void switchRenderMode(final String s) {
                activity().content().switchRenderMode();
            }
        };
    }

    public void setUrl(final UrlInformation url) {
        post(() -> {
            loadUrl("javascript:setURL('" + url.getUrl() + "');");
            loadUrl("javascript:setTitle('" + url.getTitle() + "');");
        });
    }

    public void show() {
        post(() -> setVisibility(View.VISIBLE));
    }

    public void hide() {
        post(() -> setVisibility(View.GONE));
    }
}
