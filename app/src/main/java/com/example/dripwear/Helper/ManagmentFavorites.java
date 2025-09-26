package com.example.dripwear.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.dripwear.Domain.ItemsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManagmentFavorites {
    private static ManagmentFavorites instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String FAV_PREF = "FavoritesPref";
    private static final String FAV_LIST_KEY = "favoritesList";

    // Private constructor to prevent instantiation
    private ManagmentFavorites(Context context) {
        Context appContext = context.getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences(FAV_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Thread-safe singleton instance getter
    public static synchronized ManagmentFavorites getInstance(Context context) {
        if (instance == null) {
            instance = new ManagmentFavorites(context);
        }
        return instance;
    }

    // Optional: Method to clear instance
    public static void clearInstance() {
        instance = null;
    }

    public void insertItem(ItemsModel item) {
        ArrayList<ItemsModel> favoritesList = getListFav();
        boolean exists = false;

        for (ItemsModel i : favoritesList) {
            if (i.getTitle().equals(item.getTitle())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            favoritesList.add(item);
            saveFavoritesList(favoritesList);
        }
    }

    public void removeItem(ArrayList<ItemsModel> list, int position, ChangeNumberItemsListener listener) {
        list.remove(position);
        saveFavoritesList(list);
        listener.changed();
    }

    private void saveFavoritesList(ArrayList<ItemsModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(FAV_LIST_KEY, json);
        editor.apply();
    }

    public ArrayList<ItemsModel> getListFav() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FAV_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<ItemsModel>>() {}.getType();

        if (json == null) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(json, type);
        }
    }
}