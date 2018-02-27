package org.openauto.webviewauto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.apps.auto.sdk.CarActivity;

import org.apache.commons.text.StringEscapeUtils;
import org.openauto.webviewauto.webview.WebChromeClientExtended;

public class WebViewAutoActivity extends CarActivity {

    public enum BrowserInputMode {
        URL_INPUT_MODE, CONTENT_INPUT_MODE
    }

    public enum BrowserRenderMode {
        DESKTOP_MODE, MOBILE_MODE
    }

    private static WebViewAutoActivity instance;

    public boolean browserInitialized = false;
    public String homeURL = "file:///android_asset/favorites/favorites.html";
    public String currentURL = homeURL;
    public BrowserInputMode inputMode = BrowserInputMode.URL_INPUT_MODE;
    public BrowserRenderMode renderMode = BrowserRenderMode.MOBILE_MODE;
    public String originalAgentString = "";

    private WebView webview;
    private WebView keyboard;
    private WebView menu;

    @Override
    public void onCreate(Bundle bundle) {
        instance = this;

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);
        setContentView(R.layout.auto_activity);

        this.webview = (WebView)findViewById(R.id.webview_component);
        this.keyboard = (WebView)findViewById(R.id.html5_keyboard);
        this.menu = (WebView)findViewById(R.id.html5_menu);

        switchToFragment();

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
        switchToFragment();
    }

    public void switchToFragment() {
        updateStatusBarTitle();
        updateFragmentContent();
    }

    public void updateStatusBarTitle() {
        getCarUiController().getStatusBarController().setTitle(getString(R.string.app_name));
    }

    public void updateFragmentContent() {
        updateBrowserFragment();
    }

    public void openDrawer() {
        webview.post(() -> getCarUiController().getDrawerController().openDrawer());
    }

    public void toggleURLKeyboard(String currentURL) {
        keyboard.post(() -> {
            keyboard.evaluateJavascript("javascript:setInput('" + currentURL + "');", null);
            keyboard.evaluateJavascript("javascript:$(\".input-row input\").focus();", null);
        });
        toggleKeyboard(BrowserInputMode.URL_INPUT_MODE);
    }

    public void toggleKeyboard(BrowserInputMode newInputMode) {
        this.inputMode = newInputMode;
        keyboard.post(() -> {
            if(webview.getVisibility() == View.GONE){
                webview.setVisibility(View.VISIBLE);
                menu.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
            } else {
                webview.setVisibility(View.GONE);
                menu.setVisibility(View.GONE);
                keyboard.setVisibility(View.VISIBLE);
                if(newInputMode == BrowserInputMode.CONTENT_INPUT_MODE){
                    keyboard.loadUrl("javascript:clearInput();");
                }
            }
        });
    }

    public void hideKeyboard() {
        keyboard.post(() -> {
            webview.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);
        });
    }

    public void submitForm(){
        webview.post(() -> {
            webview.evaluateJavascript("document.activeElement.form.submit();", null);
        });
    }

    public void showFavorites(){
        webview.post(() -> {
            changeURL("file:///android_asset/favorites/favorites.html");
        });
    }

    public void keyboardInputCallback(String str){
        if(BrowserInputMode.URL_INPUT_MODE == inputMode){
            webview.post(() -> {
                changeURL(str);
                hideKeyboard();
            });
        } else {
            sendStringToCar(str);
            hideKeyboard();
        }
    }

    public static void sendStringToCar(String enteredText){
        if(instance!=null) {
            instance.webview.post(() -> {
                String script = "var wwaevent = new Event('change'); " +
                        "if(document.activeElement.isContentEditable){document.activeElement.innerText = \"$1\";}" +
                        "else {document.activeElement.value = \"$1\";} document.activeElement.dispatchEvent(wwaevent);";
                script = script.replace("$1", StringEscapeUtils.escapeEcmaScript(enteredText));
                instance.webview.evaluateJavascript(script, null);
            });
        }
    }

    /**
     * TODO: Refactor - instead of using string and boolean, a Site object should be used which has a url and desktop property
     */
    public void changeURL(String url){
        //set the new url into the url input bar
        //TODO: find a way to get rid of timeout
        menu.post(() -> menu.loadUrl("javascript:setURL('"+url+"');"));

        //load the new url
        if(renderMode == BrowserRenderMode.DESKTOP_MODE){
            setDesktopMode();
        } else {
            setMobileMode();
        }
        webview.loadUrl(url);
        //remember the current url
        currentURL = url;

    }

    public void setDesktopMode(){
        WebSettings wbset=webview.getSettings();
        originalAgentString = wbset.getUserAgentString();
        wbset.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        wbset.setUseWideViewPort(true);
        wbset.setLoadWithOverviewMode(true);
    }
    public void setMobileMode(){
        WebSettings wbset=webview.getSettings();
        wbset.setUserAgentString(originalAgentString);
        wbset.setUseWideViewPort(false);
        wbset.setLoadWithOverviewMode(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment() {

        if(browserInitialized){
            return;
        }

        //content webview
        WebSettings wbset=webview.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbset.setDomStorageEnabled(true);
        wbset.setDatabaseEnabled(true);
        wbset.setGeolocationEnabled(true);
        webview.setWebChromeClient(new WebChromeClientExtended(this));
        webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                menu.post(() -> {
                    menu.loadUrl("javascript:setURL('"+url+"');");
                    menu.loadUrl("javascript:setTitle('"+view.getTitle()+"');");
                });
            }
        });

        webview.addJavascriptInterface(new HTMLInterfaceContent(this), "Android");
        CookieManager.getInstance().setAcceptThirdPartyCookies(webview,true);

        //init menu
        WebSettings menusettings= menu.getSettings();
        menusettings.setJavaScriptEnabled(true);
        menusettings.setAllowContentAccess(true);
        menusettings.setAllowFileAccess(true);
        menusettings.setAllowFileAccessFromFileURLs(true);
        menu.setWebChromeClient(new WebChromeClient());
        menu.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                //change URL when page has been loaded
                changeURL(currentURL);
            }
        });

        menu.addJavascriptInterface(new HTMLInterfaceMenu(this), "Android");
        menu.loadUrl("file:///android_asset/menu/menu.html");

        //init keyboard
        WebSettings keyboardSettings= keyboard.getSettings();
        keyboardSettings.setJavaScriptEnabled(true);
        keyboardSettings.setAllowContentAccess(true);
        keyboardSettings.setAllowFileAccess(true);
        keyboardSettings.setAllowFileAccessFromFileURLs(true);
        keyboard.setScrollbarFadingEnabled(false);
        keyboard.setWebChromeClient(new WebChromeClient());
        keyboard.setWebViewClient(new WebViewClient());
        keyboard.addJavascriptInterface(new HTMLInterfaceKeyboard(this), "Android");
        keyboard.loadUrl("file:///android_asset/keyboard/kb.html");

        browserInitialized = true;

    }

    public static void sendURLToCar(final String enteredText){
        if(instance!=null) {
            instance.changeURL(enteredText);
        }
    }
}
