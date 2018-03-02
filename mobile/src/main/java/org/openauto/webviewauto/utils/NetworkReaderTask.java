package org.openauto.webviewauto.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.openauto.webviewauto.WebViewAutoActivity;
import org.openauto.webviewauto.favorites.FavoriteEnt;

public class NetworkReaderTask extends AsyncTask<String, String, String> {

	private boolean reload;
	private FavoriteEnt f;

	public NetworkReaderTask(FavoriteEnt f, boolean reload){
		this.f=f;
		this.reload = reload;
	}

	@Override
	protected void onPostExecute(String result) {
		f.setFavicon(result);
		WebViewAutoActivity.refresh();
	}

	@Override
	protected String doInBackground(String... s) {
		try {
			String iconUrl = IconUtil.getIconURL(f.getUrl());
			Bitmap bitmap = IconUtil.getBitmapFromURL(iconUrl);
			return IconUtil.getBase64Image(bitmap);
		}catch(Exception e){
			Log.e("WebViewAuto Network",Log.getStackTraceString(e));
		}

		return "nofavimg.png";
	}

}
