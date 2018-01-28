package com.hungryhackers.stocks.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;

/**
 * Created by YourFather on 22-01-2018.
 */

public class StockViewModel extends ViewModel {
    private static final String TAG = "StockViewModel";

    private static MutableLiveData<ArrayList<Stock>> stockList;
    private static MutableLiveData<ArrayList<String>> symbolList;
    private static MutableLiveData<ArrayList<StockSymbol>> symbolSearchResponse;
    private StockRepository stockRepo;

    public void init(Boolean firstLogin, StockRepository stockRepository){
        stockRepo = stockRepository;
        if (symbolList == null && stockList == null) {
            symbolList = stockRepo.getSymbolList(firstLogin);
            stockList = stockRepo.getStockList(StockUtils.convertToString(symbolList.getValue()));
            Log.i(TAG, "stockList initialised for the first time");
        }
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

        stockRepo.getStockList(symbol);
    }
}
