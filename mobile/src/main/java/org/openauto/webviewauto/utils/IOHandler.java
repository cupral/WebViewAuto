package org.openauto.webviewauto.utils;

import android.content.Context;

import org.openauto.webviewauto.WebViewContext;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@SuppressWarnings("SameParameterValue")
public class IOHandler {

    public Object readObject(String fileName) {
        try {
            FileInputStream fis = WebViewContext.getAppContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object obj = is.readObject();
            is.close();
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    public void saveObject(Object obj, String fileName) {
        try {
            FileOutputStream fos = WebViewContext.getAppContext().openFileOutput(fileName,Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
