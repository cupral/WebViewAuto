package org.openauto.webviewauto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.apps.auto.sdk.CarActivity;

import org.apache.commons.text.StringEscapeUtils;
import org.openauto.webviewauto.favorites.FavoriteManager;
import org.openauto.webviewauto.fragments.BrowserFragment;
import org.openauto.webviewauto.webview.WebChromeClientExtended;

public class WebViewAutoActivity extends CarActivity {

    public enum BrowserInputMode {
        URL_INPUT_MODE, CONTENT_INPUT_MODE
    }

    public enum BrowserRenderMode {
        DESKTOP_MODE, MOBILE_MODE
    }

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";
    private String mCurrentFragmentTag;

    public boolean browserInitialized = false;
    public String homeURL = "file:///android_asset/favorites/favorites.html";
    public String currentURL = homeURL;
    public BrowserInputMode inputMode = BrowserInputMode.URL_INPUT_MODE;
    public BrowserRenderMode renderMode = BrowserRenderMode.MOBILE_MODE;
    public String originalAgentString = "";

    @Override
    public void onCreate(Bundle bundle) {

        //android.os.Debug.waitForDebugger();

        ActivityAccessHelper.getInstance().setActivity(this);

        if(ActivityAccessHelper.getInstance().getFavoriteManager() == null){
            ActivityAccessHelper.getInstance().setFavoriteManager(new FavoriteManager(this));
        }

        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);
        setContentView(R.layout.activity_car_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        BrowserFragment browserFragment = new BrowserFragment();

        //Add fragments
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, browserFragment, BrowserFragment.TAG)
                .detach(browserFragment)
                .commitNow();

        String initialFragmentTag = BrowserFragment.TAG;

        if (bundle != null && bundle.containsKey(CURRENT_FRAGMENT_KEY)) {
            initialFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY);
        }
        switchToFragment(initialFragmentTag);

        //Status bar controller
        getCarUiController().getMenuController().hideMenuButton();
        getCarUiController().getStatusBarController().hideMicButton();
        getCarUiController().getStatusBarController().hideTitle();
        getCarUiController().getStatusBarController().hideAppHeader();
        getCarUiController().getStatusBarController().setAppBarAlpha(0f);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks,
                false);

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        switchToFragment(mCurrentFragmentTag);
    }

    public void switchToFragment(String tag) {
        if (tag.equals(mCurrentFragmentTag)) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment = mCurrentFragmentTag == null ? null : manager.findFragmentByTag(mCurrentFragmentTag);
        Fragment newFragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.detach(currentFragment);
        }
        transaction.attach(newFragment);
        transaction.commit();
        mCurrentFragmentTag = tag;
    }

    private final FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            updateStatusBarTitle();
            updateFragmentContent(f);
        }
    };

    public void updateStatusBarTitle() {
        CarFragment fragment = (CarFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        getCarUiController().getStatusBarController().setTitle(fragment.getTitle());
    }

    public void updateFragmentContent(Fragment fragment) {
        if(fragment instanceof BrowserFragment){
            updateBrowserFragment(fragment);
        }
    }

    public void openDrawer() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.post(() -> getCarUiController().getDrawerController().openDrawer());
    }

    public void toggleURLKeyboard(String currentURL) {
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        keyboard.post(() -> {
            keyboard.evaluateJavascript("javascript:setInput('" + currentURL + "');", null);
            keyboard.evaluateJavascript("javascript:$(\".input-row input\").focus();", null);
        });
        toggleKeyboard(BrowserInputMode.URL_INPUT_MODE);
    }

    public void toggleKeyboard(BrowserInputMode newInputMode) {
        this.inputMode = newInputMode;
        WebView webview = (WebView)findViewById(R.id.webview_component);
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        WebView menu = (WebView)findViewById(R.id.html5_menu);
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

    public void showKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        WebView menu = (WebView)findViewById(R.id.html5_menu);
        keyboard.post(() -> {
            webview.setVisibility(View.GONE);
            menu.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);
        });
    }

    public void hideKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        WebView menu = (WebView)findViewById(R.id.html5_menu);
        keyboard.post(() -> {
            webview.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);
        });
    }

    public void submitForm(){
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.post(() -> {
            webview.evaluateJavascript("document.activeElement.form.submit();", null);
        });
    }

    public void showFavorites(){
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.post(() -> {
            changeURL("file:///android_asset/favorites/favorites.html");
        });
    }

    public void sendURLToCar(String enteredText){
        changeURL(enteredText);
    }

    public void keyboardInputCallback(String str){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        if(BrowserInputMode.URL_INPUT_MODE == inputMode){
            wbb.post(() -> {
                changeURL(str);
                hideKeyboard();
            });
        } else {
            sendStringToCar(str);
            hideKeyboard();
        }
    }

    public void sendStringToCar(String enteredText){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        wbb.post(() -> {
            String script = "var wwaevent = new Event('change'); " +
                    "if(document.activeElement.isContentEditable){document.activeElement.innerText = \"$1\";}" +
                    "else {document.activeElement.value = \"$1\";} document.activeElement.dispatchEvent(wwaevent);";
            script = script.replace("$1", StringEscapeUtils.escapeEcmaScript(enteredText));
            wbb.evaluateJavascript(script, null);
        });
    }

    /**
     * TODO: Refactor - instead of using string and boolean, a Site object should be used which has a url and desktop property
     */
    public void changeURL(String url){
        //set the new url into the url input bar
        WebView html5_menu = (WebView)findViewById(R.id.html5_menu);
        //TODO: find a way to get rid of timeout
        html5_menu.post(() -> html5_menu.loadUrl("javascript:setURL('"+url+"');"));

        //load the new url
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        if(renderMode == BrowserRenderMode.DESKTOP_MODE){
            setDesktopMode();
        } else {
            setMobileMode();
        }
        wbb.loadUrl(url);
        //remember the current url
        currentURL = url;

    }

    public void setDesktopMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        originalAgentString = wbset.getUserAgentString();
        wbset.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        wbset.setUseWideViewPort(true);
        wbset.setLoadWithOverviewMode(true);
    }
    public void setMobileMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setUserAgentString(originalAgentString);
        wbset.setUseWideViewPort(false);
        wbset.setLoadWithOverviewMode(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment(Fragment fragment) {

        if(browserInitialized){
            return;
        }

        //load web views
        WebView contentWebView = (WebView)findViewById(R.id.webview_component);
        final WebView menu = (WebView)findViewById(R.id.html5_menu);
        final WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);

        //content webview
        WebSettings wbset=contentWebView.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbset.setDomStorageEnabled(true);
        wbset.setDatabaseEnabled(true);
        contentWebView.setWebChromeClient(new WebChromeClientExtended(this));
        contentWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                menu.post(() -> {
                    menu.loadUrl("javascript:setURL('"+url+"');");
                    menu.loadUrl("javascript:setTitle('"+view.getTitle()+"');");
                });
            }
        });

        contentWebView.addJavascriptInterface(new HTMLInterfaceContent(this), "Android");
        CookieManager.getInstance().setAcceptThirdPartyCookies(contentWebView,true);

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

}
