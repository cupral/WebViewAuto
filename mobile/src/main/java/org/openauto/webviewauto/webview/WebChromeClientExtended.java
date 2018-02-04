package org.openauto.webviewauto.webview;


import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import org.openauto.webviewauto.R;
import org.openauto.webviewauto.WebViewAutoActivity;

public class WebChromeClientExtended extends WebChromeClient {

    private WebView menuWebView;
    private WebView contentWebView;
    private FrameLayout customViewContainer;

    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;

    public WebChromeClientExtended(WebViewAutoActivity activity){
        this.menuWebView = (WebView)activity.findViewById(R.id.html5_menu);
        this.contentWebView = (WebView)activity.findViewById(R.id.webview_component);
        this.customViewContainer = (FrameLayout) activity.findViewById(R.id.customview_component);
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        // callback.invoke(String origin, boolean allow, boolean remember);
        callback.invoke(origin, true, false);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mCustomView = view;

        menuWebView.setVisibility(View.GONE);
        contentWebView.setVisibility(View.GONE);
        customViewContainer.setVisibility(View.VISIBLE);

        customViewContainer.addView(view);
        customViewCallback = callback;

        //super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {

        super.onHideCustomView();
        if (mCustomView == null)
            return;

        menuWebView.setVisibility(View.VISIBLE);
        contentWebView.setVisibility(View.VISIBLE);
        customViewContainer.setVisibility(View.GONE);

        // Hide the custom view.
        mCustomView.setVisibility(View.GONE);

        // Remove the custom view from its container.
        customViewContainer.removeView(mCustomView);
        customViewCallback.onCustomViewHidden();

        mCustomView = null;

        //super.onHideCustomView();
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }
}
