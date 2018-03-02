package org.openauto.webviewauto.utils;

import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Created by ljannace on 28/02/18.
 */

public class HtmlUtils {
    private static Gson gson = new Gson();

    public static String toJavascript(final Object src){
        return toJavascript(gson.toJson(src));
    }

    public static String toJavascript(final String src){
        return StringEscapeUtils.escapeEcmaScript(src);
    }
}
