package org.openauto.webviewauto.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import org.openauto.webviewauto.WebViewAutoActivity;

/**
 * Created by ljannace on 28/02/18.
 */

public class KeyboardWebView extends BaseWebView {

    private boolean urlInputMode;

    public KeyboardWebView(final Context context) {
        super(context);
    }

    public KeyboardWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardWebView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KeyboardWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public KeyboardWebView(final Context context, final AttributeSet attrs, final int defStyleAttr, final boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void init(final WebSettings settings) {
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        setScrollbarFadingEnabled(false);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());
        loadUrl("file:///android_asset/keyboard/kb.html");
    }

    @Override
    protected Object createJavascriptInterface() {
        return new Object() {
            @JavascriptInterface
            public void submitInput(final String str) {
                if (urlInputMode) {
                    WebViewAutoActivity.sendURLToCar(str);
                } else {
                    WebViewAutoActivity.sendStringToCar(str);
                }

                hide();
            }

            @JavascriptInterface
            public void hideKeyboard(final String str) {
                hide();
            }
        };
    }

    public void showForInput() {
        post(() -> {
            setVisibility(View.VISIBLE);
            loadUrl("javascript:clearInput();");
            urlInputMode = false;
        });
    }

    public void showForUrl() {
        final String url = activity().content().getCurrentUrl().getUrl();
        post(() -> {
            setVisibility(View.VISIBLE);
            evaluateJavascript("javascript:setInput('" + url + "');", null);
            evaluateJavascript("javascript:$(\".input-row input\").focus();", null);
            urlInputMode = true;
        });
    }

    public void hide() {
        post(() -> setVisibility(View.GONE));
    }


}
