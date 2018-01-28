package com.hungryhackers.stocks.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hungryhackers.stocks.network.StockRepository;

import java.util.ArrayList;

/**
 * Created by YourFather on 28-01-2018.
 */

public class SearchViewModel extends ViewModel {
    private MutableLiveData<ArrayList<StockSymbol>> searchResponse;
    private StockRepository repo;

    public void init(StockRepository repo){
        this.repo = repo;
    }

//    public MutableLiveData<ArrayList<SymbolResponse>> getSearchResponse() {
//        return searchResponse;
//    }
}
