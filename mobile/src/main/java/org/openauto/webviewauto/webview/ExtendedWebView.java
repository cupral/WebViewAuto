package org.openauto.webviewauto.webview;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;

public class ExtendedWebView extends WebView {

    private InputConnection inputConnection;

    public ExtendedWebView(Context context) {
        super(context);
    }

    public ExtendedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExtendedWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return super.onCheckIsTextEditor();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        this.inputConnection = super.onCreateInputConnection(outAttrs);
        return inputConnection;
    }

    public InputConnection getInputConnection() {
        return inputConnection;
    }
}
