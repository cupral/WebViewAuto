package org.openauto.webviewauto;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.apps.auto.sdk.CarActivity;

import org.openauto.webviewauto.webview.ContentWebView;
import org.openauto.webviewauto.webview.KeyboardWebView;
import org.openauto.webviewauto.webview.MenuWebView;
import org.openauto.webviewauto.webview.UrlInformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WebViewAutoActivity extends CarActivity {

    private static WebViewAutoActivity instance;

    private ContentWebView content;
    private KeyboardWebView keyboard;
    private MenuWebView menu;
    private ViewGroup container, contentContainer;
    private View fullsreenView;

    public static WebViewAutoActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle bundle) {
        instance = this;

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);
        setContentView(R.layout.activity_auto_main);

        this.content = (ContentWebView) findViewById(R.id.content);
        this.keyboard = (KeyboardWebView) findViewById(R.id.keyboard);
        this.menu = (MenuWebView) findViewById(R.id.menu);
        this.container = (ViewGroup) findViewById(R.id.container);
        this.contentContainer = (ViewGroup) findViewById(R.id.content_container);

        //Status bar controller
        getCarUiController().getMenuController().hideMenuButton();
        getCarUiController().getStatusBarController().hideMicButton();
        getCarUiController().getStatusBarController().hideTitle();
        getCarUiController().getStatusBarController().hideAppHeader();
        getCarUiController().getStatusBarController().setAppBarAlpha(0f);
    }

    @Override
    public void onStart() {
        super.onStart();
        getCarUiController().getStatusBarController().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onResume() {
        super.onResume();
        content.init();
        menu.init();
        keyboard.init();
    }

    @Override
    public void onStop() {
        this.content = null;
        this.keyboard = null;
        this.menu = null;
        this.container = null;
        this.contentContainer = null;
        this.fullsreenView = null;
        instance = null;
        super.onStop();
    }

    public ContentWebView content() {
        return content;
    }

    public KeyboardWebView keyboard() {
        return keyboard;
    }

    public MenuWebView menu() {
        return menu;
    }

    public void openDrawer() {
        content.post(() -> getCarUiController().getDrawerController().openDrawer());
    }

    public void setUrl(final UrlInformation url) {
        menu.setUrl(url);
        content.setUrl(url);
    }

    public boolean addFullscreenView(final View view) {
        if (fullsreenView != null || view == null) {
            return false;
        }
        contentContainer.setVisibility(GONE);
        container.addView(fullsreenView = view);
        return true;
    }

    public boolean removeFullscreenView() {
        if (fullsreenView == null) {
            return false;
        }
        contentContainer.setVisibility(VISIBLE);
        container.removeView(fullsreenView);
        return true;
    }

    public static void sendURLToCar(final String enteredText) {
        if (instance != null) {
            instance.setUrl(instance.content.getCurrentUrl().setUrl(enteredText));
        }
    }

    public static void sendStringToCar(final String enteredText) {
        if (instance != null) {
            instance.content.type(enteredText);
        }
    }

    public static void refresh() {
        if (instance != null) {
            instance.content.refresh();
        }
    }
}
