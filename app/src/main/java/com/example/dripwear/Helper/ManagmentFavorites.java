package com.example.dripwear.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.dripwear.Domain.ItemsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManagmentFavorites {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String FAV_PREF = "FavoritesPref";
    private static final String FAV_LIST_KEY = "favoritesList";

    public ManagmentFavorites(Context context) {
        //Initialize SharedPreferences
        sharedPreferences = context.getSharedPreferences(FAV_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void insertItem(ItemsModel item) {
        ArrayList<ItemsModel> favoritesList = getListFav();
        boolean exists = false;

        //Check if item exists
        for (ItemsModel i : favoritesList) {
            if (i.getTitle().equals(item.getTitle())) {
                exists = true;
                break;
            }
        }

        //Add if new
        if (!exists) {
            favoritesList.add(item);
            saveFavoritesList(favoritesList);
        }
    }

    public void removeItem(ArrayList<ItemsModel> list, int position, ChangeNumberItemsListener listener) {
        //Remove item
        list.remove(position);
        //Save list, trigger listener
        saveFavoritesList(list);
        listener.changed();
    }

    private void saveFavoritesList(ArrayList<ItemsModel> list) {
        //Convert list to JSON
        Gson gson = new Gson();
        String json = gson.toJson(list);
        //Store JSON string
        editor.putString(FAV_LIST_KEY, json);
        editor.apply();
    }

    public ArrayList<ItemsModel> getListFav() {
        //Get JSON string
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FAV_LIST_KEY, null);
        //Define list type for Gson
        Type type = new TypeToken<ArrayList<ItemsModel>>() {}.getType();

        //Return list or new list
        if (json == null) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(json, type);
        }
    }
}
