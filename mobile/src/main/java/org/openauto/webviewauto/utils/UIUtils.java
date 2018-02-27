package org.openauto.webviewauto.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;

public class UIUtils {
    public static void showSnackbar(Activity act, String msg, int length){
        Snackbar.make(act.findViewById(android.R.id.content), msg, length).show();
    }
}
