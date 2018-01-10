package org.openauto.webviewauto;

import android.util.Log;

import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;

import org.openauto.webviewauto.favorites.FavoriteEnt;
import org.openauto.webviewauto.favorites.FavoriteManager;

public class MainMenuHandler {

    public static void buildMainMenu(final WebViewAutoActivity activity){

        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity, mainMenu));

        mainMenu.addMenuItem("MENU_HOME", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_home))
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_BACK", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_back))
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_FAVORITES", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_favorites))
                .setType(MenuItem.Type.SUBMENU)
                .build());

        FavoriteManager favoriteManager = activity.favoriteManager;

        ListMenuAdapter favMenu = new ListMenuAdapter();
        favMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity, mainMenu));
        for(FavoriteEnt fav : favoriteManager.favorites){
            favMenu.addMenuItem(fav.getId(), new MenuItem.Builder()
                    .setTitle(fav.getTitle())
                    .setType(MenuItem.Type.ITEM)
                    .build());
        }
        mainMenu.addSubmenu("MENU_FAVORITES", favMenu);

        MenuController menuController = activity.getCarUiController().getMenuController();
        menuController.setRootMenuAdapter(mainMenu);
        menuController.showMenuButton();

    }


    public static ListMenuAdapter.MenuCallbacks createMenuCallbacks(final WebViewAutoActivity activity, ListMenuAdapter mainMenu){

        final ListMenuAdapter.MenuCallbacks mMenuCallbacks = new ListMenuAdapter.MenuCallbacks() {
            @Override
            public void onMenuItemClicked(String name) {
                Log.i("Test", name);

                if("MENU_HOME".equals(name)){
                    activity.changeURL(activity.homeURL, false);
                }
                if("MENU_BACK".equals(name)){
                    int historySize = activity.urlHistory.size();
                    int newIndex = historySize - 2;
                    if(newIndex > 0 && newIndex < historySize){
                        String newURL = activity.urlHistory.get(newIndex);
                        activity.changeURL(newURL, false);
                    }
                }
                if(name.startsWith("MENU_FAVORITES_")){
                   FavoriteManager favoriteManager = activity.favoriteManager;
                   FavoriteEnt favorite = favoriteManager.getFavoriteById(name);
                   activity.changeURL(favorite.getUrl(), favorite.getDesktop());
                }
            }

            @Override
            public void onEnter() {
            }

            @Override
            public void onExit() {
                activity.updateStatusBarTitle();
            }
        };
        return mMenuCallbacks;
    }


}
