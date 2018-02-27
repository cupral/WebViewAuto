package org.openauto.webviewauto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openauto.webviewauto.favorites.FavoriteEnt;
import org.openauto.webviewauto.favorites.FavoriteManager;
import org.openauto.webviewauto.utils.NetworkReaderTask;
import org.openauto.webviewauto.utils.UIUtils;

/**
 * Activity for phone
 */
public class WebViewPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);

        reloadFavList();

        final Button add_favorite_button = findViewById(R.id.add_favorite_button);
        add_favorite_button.setOnClickListener(v -> {
            final EditText new_fav_title = findViewById(R.id.new_fav_title);
            final EditText new_fav_url = findViewById(R.id.new_fav_url);
            final CheckBox new_fav_desktop_mode = findViewById(R.id.new_fav_desktop_mode);
            if(!new_fav_title.getText().toString().isEmpty() && !new_fav_url.getText().toString().isEmpty()){
                final FavoriteEnt newFav = new FavoriteEnt("MENU_FAVORITES_" + new_fav_title.getText().toString(), new_fav_title.getText().toString(), new_fav_url.getText().toString(), new_fav_desktop_mode.isChecked());

                //Load icon
                final NetworkReaderTask nt = new NetworkReaderTask(this, newFav, false);
                nt.execute();

                FavoriteManager.getInstance().addFavorite(newFav);
                FavoriteManager.getInstance().persistFavorites();
                reloadFavList();
            } else {
                UIUtils.showSnackbar(this, "Title or URL is empty", 1000);
            }

        });


        final EditText url_to_car_text = findViewById(R.id.url_to_car_text);
        final Button url_to_car_button = findViewById(R.id.url_to_car_button);
        url_to_car_button.setOnClickListener(v -> {
            WebViewAutoActivity.sendURLToCar(url_to_car_text.getText().toString());
        });

        final EditText text_to_car_text = findViewById(R.id.text_to_car_text);
        final Button text_to_car_button = findViewById(R.id.text_to_car_button);
        text_to_car_button.setOnClickListener(v -> {
            WebViewAutoActivity.sendStringToCar(text_to_car_text.getText().toString());
        });

    }

    private void reloadFavList(){

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout favorite_container = findViewById(R.id.favorite_container);
        favorite_container.removeAllViews();

        for(FavoriteEnt e : FavoriteManager.getInstance().favorites){

            View favItemView = inflater.inflate(R.layout.activity_phone_main_fav_item, null);
            TextView fav_item_title_tv = favItemView.findViewById(R.id.fav_item_title_tv);
            fav_item_title_tv.setText(e.getTitle());
            TextView fav_item_url_tv = favItemView.findViewById(R.id.fav_item_url_tv);
            fav_item_url_tv.setText(e.getUrl());
            CheckBox fav_item_url_cb = favItemView.findViewById(R.id.fav_item_url_cb);
            fav_item_url_cb.setChecked(e.getDesktop());
            Button fav_item_removebtn = favItemView.findViewById(R.id.fav_item_removebtn);
            fav_item_removebtn.setTag(e);
            fav_item_removebtn.setOnClickListener(v -> {
                FavoriteManager.getInstance().removeFavorite((FavoriteEnt)v.getTag());
                FavoriteManager.getInstance().persistFavorites();
                reloadFavList();
            });
            favorite_container.addView(favItemView);

        }
    }


}
