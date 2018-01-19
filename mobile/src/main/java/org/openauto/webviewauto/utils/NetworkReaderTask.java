package org.openauto.webviewauto.utils;

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
			return iconUrl;
		}catch(Exception e){
			Log.e("WebViewAuto Network",Log.getStackTraceString(e));
		}
		return null;
	}

}
