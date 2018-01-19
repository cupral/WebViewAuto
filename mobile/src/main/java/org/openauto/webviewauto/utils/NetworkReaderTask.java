package org.openauto.webviewauto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import org.openauto.webviewauto.R;
import org.openauto.webviewauto.WebViewAutoActivity;
import org.openauto.webviewauto.favorites.FavoriteEnt;

public class NetworkReaderTask extends AsyncTask<String, String, String> {

	private boolean reload;
	private FavoriteEnt f;
	private Context context;

	public NetworkReaderTask(Context context, FavoriteEnt f, boolean reload){
		this.context = context;
		this.f=f;
		this.reload = reload;

	}

	@Override
	protected void onPostExecute(String result) {
		f.setFavicon(result);
		//call reload on JS when a new image has been loaded
		if(context instanceof WebViewAutoActivity){
			WebViewAutoActivity activity = (WebViewAutoActivity) context;
			WebView webview = (WebView)activity.findViewById(R.id.webview_component);
			if(reload) {
				webview.post(webview::reload);
			}
		}
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
