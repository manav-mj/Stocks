package com.hungryhackers.stocks.models;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.hungryhackers.stocks.MainActivity;
import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by YourFather on 22-01-2018.
 */

public class StockViewModel extends ViewModel {
    private static final String TAG = "StockViewModel";

    private MutableLiveData<ArrayList<Stock>> stockList;
    private MutableLiveData<ArrayList<String>> symbolList;
    private MutableLiveData<ArrayList<StockSymbol>> symbolSearchResponse;
    private StockRepository stockRepo;

    public void init(Boolean firstLogin, StockRepository stockRepository){
        stockRepo = stockRepository;
        symbolList = stockRepo.getSymbolList(firstLogin);
        stockList = stockRepo.getStockList(StockUtils.convertToString(symbolList.getValue()), null);
        Log.i(TAG, "stockList initialised for the first time");
    }

    public MutableLiveData<ArrayList<Stock>> getStockList() {
        return stockList;
    }

    public MutableLiveData<ArrayList<String>> getSymbolList() {
        return symbolList;
    }

    public MutableLiveData<ArrayList<StockSymbol>> getSymbolSearchResponse() {
        return symbolSearchResponse;
    }

    public Boolean dataIsNull(){
        return getStockList().getValue() == null;
    }

    public void fetchSymbolForName(String companyName) {
        symbolSearchResponse = stockRepo.getSymbolsForName(companyName);
    }

    public void addStockWithSymbol(String symbol){
        symbolList.getValue().add(symbol);
        symbolList.setValue(symbolList.getValue());

        stockRepo.getStockList(symbol, stockList);
    }
}
