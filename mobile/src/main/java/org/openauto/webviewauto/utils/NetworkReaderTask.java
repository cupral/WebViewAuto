package org.openauto.webviewauto.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.openauto.webviewauto.favorites.FavoriteEnt;

public class NetworkReaderTask extends AsyncTask<String, String, String> {

	private FavoriteEnt f;
	public NetworkReaderTask(FavoriteEnt f){
		this.f=f;
	}

	@Override
	protected void onPostExecute(String result) {
		f.setFavicon(result);
	}

	@Override
	protected String doInBackground(String... s) {
		try {
			String iconUrl = IconUtil.getIconURL(f.getUrl());
			Bitmap bitmap = IconUtil.getBitmapFromURL(iconUrl);
			String base64 = IconUtil.getBase64Image(bitmap);
			//Log.e("WebViewAuto Network",base64);
			return base64;
		}catch(Exception e){
			Log.e("WebViewAuto Network",Log.getStackTraceString(e));
		}
		return null;
	}

}
