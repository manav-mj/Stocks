package com.hungryhackers.stocks.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.hungryhackers.stocks.MainActivity;
import com.hungryhackers.stocks.network.ApiClient;
import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hungryhackers.stocks.MainActivity.STOCK_DELIMITER_FOR_SP;

/**
 * Created by YourFather on 22-01-2018.
 */

public class StockViewModel extends ViewModel {
    private static final String TAG = "StockViewModel";
    private MutableLiveData<ArrayList<Stock>> stockList;
    private MutableLiveData<ArrayList<String>> symbolList;
    private StockRepository stockRepo;

    public void init(Boolean firstLogin, StockRepository stockRepository){
        stockRepo = stockRepository;
        symbolList = stockRepo.getSymbolList(firstLogin);
        stockList = stockRepo.getStockList(StockUtils.convertToString(symbolList.getValue()));
    }

    public MutableLiveData<ArrayList<Stock>> getStockList() {
        return stockList;
    }

    public MutableLiveData<ArrayList<String>> getSymbolList() {
        return symbolList;
    }

    public Boolean dataIsNull(){
        return getStockList().getValue() == null;
    }
}
