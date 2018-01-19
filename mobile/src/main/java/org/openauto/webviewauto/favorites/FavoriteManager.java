package org.openauto.webviewauto.favorites;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.openauto.webviewauto.WebViewContext;
import org.openauto.webviewauto.utils.IOHandler;
import org.openauto.webviewauto.utils.NetworkReaderTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    private static volatile FavoriteManager instance = null;
    public List<FavoriteEnt> favorites;

    public static FavoriteManager getInstance() {
        if (instance == null) {
            synchronized(FavoriteManager.class) {
                if (instance == null) {
                    instance = new FavoriteManager();
                    instance.readFavorites();
                }
            }
        }
        return instance;
    }

    private List<FavoriteEnt> getDefaultFavorites(){
        List<FavoriteEnt> favorites = new ArrayList<>();
        favorites.add(new FavoriteEnt("MENU_FAVORITES_DuckDuckGo","DuckDuckGo","https://duckduckgo.com/", false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_Google","Google","https://www.google.com/",false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_YouTube","YouTube","https://www.youtube.com/",false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_Wikipedia","Wikipedia","https://www.wikipedia.org/", false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_BBC","BBC","http://www.bbc.com", false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_Tagesschau","Tagesschau","https://www.tagesschau.de/", false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_RT","RT","https://www.rt.com/", false));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_CNN","CNN","https://edition.cnn.com/", false));

        for(FavoriteEnt e : favorites){
            NetworkReaderTask nt = new NetworkReaderTask(WebViewContext.getAppContext(), e, true);
            nt.execute();
        }

        return favorites;
    }

    public FavoriteEnt getFavoriteById(String id){
        for(FavoriteEnt f : favorites){
            if(f.getId().equals(id)){
                return f;
            }
        }
        return null;
    }

    public void addFavorite(FavoriteEnt newFav){
        this.favorites.add(newFav);
    }

    public void removeFavorite(FavoriteEnt favToRemove){
        this.favorites.remove(favToRemove);
    }

    public void persistFavorites(){
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        IOHandler ioHandler = new IOHandler();
        ioHandler.saveObject(json, "favorites.json");
    }

    private void readFavorites(){
        IOHandler ioHandler = new IOHandler();
        String favoritesJson = (String)ioHandler.readObject("favorites.json");
        if(favoritesJson == null){
            favorites = getDefaultFavorites();
        } else {
            Gson gson = new Gson();
            Type favListType = new TypeToken<List<FavoriteEnt>>() {}.getType();
            favorites = gson.fromJson(favoritesJson, favListType);
        }
    }

    public void resetFavorites(){
        favorites = getDefaultFavorites();
        persistFavorites();
    }


}
