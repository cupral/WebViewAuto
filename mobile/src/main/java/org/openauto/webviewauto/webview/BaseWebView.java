package org.openauto.webviewauto.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.openauto.webviewauto.WebViewAutoActivity;

/**
 * Created by ljannace on 28/02/18.
 */

public abstract class BaseWebView extends WebView {

    protected static UrlInformation FAVORITES = new UrlInformation("file:///android_asset/favorites/favorites.html", false);

    private boolean initialized;

    public BaseWebView(final Context context) {
        super(context);
    }

    public BaseWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BaseWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    protected final WebViewAutoActivity activity() {
        return WebViewAutoActivity.getInstance();
    }

    @SuppressLint("JavascriptInterface")
    public final void init() {
        if (initialized) {
            return;
        }

        addJavascriptInterface(createJavascriptInterface(), "Android");

        init(getSettings());

        initialized = true;
    }

    protected abstract void init(final WebSettings settings);

    protected abstract @JavascriptInterface Object createJavascriptInterface();

}
