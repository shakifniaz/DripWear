package com.example.dripwear.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dripwear.Domain.BannerModel;
import com.example.dripwear.Domain.CategoryModel;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Repository.MainRepository;

import java.util.ArrayList;
import java.util.Calendar;

public class MainViewModel extends ViewModel {

    private final MainRepository repository= new MainRepository();

    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return repository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner(){
        return repository.loadBanner();
    }

    public LiveData<ArrayList<ItemsModel>> loadPopular(){
        return repository.loadPopular();
    }
}
