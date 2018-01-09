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

import org.openauto.webviewauto.fragments.BrowserFragment;

import java.util.ArrayList;
import java.util.List;

public class WebViewAutoActivity extends CarActivity {

    public enum BrowserInputMode {
        URL_INPUT_MODE, CONTENT_INPUT_MODE
    }

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";
    private String mCurrentFragmentTag;

    public String homeURL = "https://duckduckgo.com";
    public String currentURL = homeURL;
    public BrowserInputMode inputMode = BrowserInputMode.URL_INPUT_MODE;
    public String originalAgentString = "";
    public String currentBrowserMode = "MOBILE";

    public List<String> urlHistory = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {

        //android.os.Debug.waitForDebugger();

        ActivityAccessHelper.getInstance().activity = this;

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

        //Build main menu
        MainMenuHandler.buildMainMenu(this);

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
        keyboard.post(() -> {
            if(webview.getVisibility() == View.GONE){
                webview.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
            } else {
                webview.setVisibility(View.GONE);
                keyboard.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        keyboard.post(() -> {
            webview.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);
        });
    }

    public void hideKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
        keyboard.post(() -> {
            webview.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);
        });
    }

    public void submitForm(){
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.post(() -> {
            webview.evaluateJavascript("document.activeElement.form.submit();", null);
        });
    }

    public void sendURLToCar(String enteredText){
        changeURL(enteredText);
    }

    public void keyboardInputCallback(String str){
        if(BrowserInputMode.URL_INPUT_MODE == inputMode){
            changeURL(str);
        } else {
            sendStringToCar(str);
            hideKeyboard();
        }
    }

    public void sendStringToCar(String enteredText){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        wbb.post(() -> wbb.evaluateJavascript("document.activeElement.value = '" + enteredText + "';", null));
    }

    public void changeURL(String url){
        //set the new url into the url input bar
        WebView html5_menu = (WebView)findViewById(R.id.html5_menu);
        //TODO: find a way to get rid of timeout
        html5_menu.post(() -> html5_menu.loadUrl("javascript:setTimeout(function(){setURL('"+url+"')},200);"));

        //load the new url
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        //Add URLs for Desktop mode here
        List<String> desktopModeURLs = new ArrayList<>();
        //desktopModeURLs.add("");
        if(desktopModeURLs.contains(url) && !currentBrowserMode.equals("DESKTOP")){
            setDesktopMode();
            currentBrowserMode = "DESKTOP";
        } else {
            setMobileMode();
            currentBrowserMode = "MOBILE";
        }
        wbb.loadUrl(url);
        //remember the current url
        currentURL = url;
        //add url to history if last item is not already in the history
        if(!urlHistory.isEmpty() && !urlHistory.get(urlHistory.size()-1).equals(url)){
            urlHistory.add(url);
        }
    }

    private void setDesktopMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        originalAgentString = wbset.getUserAgentString();
        wbset.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        wbset.setUseWideViewPort(true);
        wbset.setLoadWithOverviewMode(true);
    }
    private void setMobileMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setUserAgentString(originalAgentString);
        wbset.setUseWideViewPort(false);
        wbset.setLoadWithOverviewMode(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment(Fragment fragment) {

        //load web view
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbset.setDomStorageEnabled(true);
        wbb.setWebChromeClient(new WebChromeClient());
        wbb.setWebViewClient(new WebViewClient());
        CookieManager.getInstance().setAcceptThirdPartyCookies(wbb,true);

        //init menu
        final WebView menu = (WebView)findViewById(R.id.html5_menu);
        WebSettings menusettings= menu.getSettings();
        menusettings.setJavaScriptEnabled(true);
        menusettings.setAllowContentAccess(true);
        menusettings.setAllowFileAccess(true);
        menusettings.setAllowFileAccessFromFileURLs(true);
        menu.setWebChromeClient(new WebChromeClient());
        menu.setWebViewClient(new WebViewClient());
        menu.addJavascriptInterface(new HTMLInterfaceMenu(this), "Android");
        menu.loadUrl("file:///android_asset/menu/menu.html");

        //init keyboard
        final WebView keyboard = (WebView)findViewById(R.id.html5_keyboard);
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

        //set initial url
        changeURL(currentURL);

    }

}
