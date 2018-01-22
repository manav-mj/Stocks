package com.hungryhackers.stocks.network;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.Stock;
import com.hungryhackers.stocks.models.SymbolResponse;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.hungryhackers.stocks.MainActivity.STOCK_DELIMITER_FOR_SP;
import static com.hungryhackers.stocks.StringConstants.STOCKSYMBOLS;
import static com.hungryhackers.stocks.StringConstants.SYMBOL_SEARCH_API_URL;

/**
 * Created by YourFather on 22-01-2018.
 */

public class StockRepository {
    private static final String TAG = "StockRepository";

    Context mContext;

    public StockRepository(Context mContext) {
        this.mContext = mContext;
    }

    public MutableLiveData<ArrayList<Stock>> getStockList(String stockSymbols){

        MutableLiveData<ArrayList<Stock>> stockList = new MutableLiveData<>();

        ApiClient.getStockSearchApiInterface()
                .getStockBatch(stockSymbols)
                .enqueue(new Callback<BatchResponse>() {
                    @Override
                    public void onResponse(Call<BatchResponse> call, Response<BatchResponse> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Stock batch data successfully fetched");

                            BatchResponse batch = response.body();
                            if (batch == null) {
                                Log.e(TAG, "Stock batch response body null");
                                return;
                            }
                            stockList.setValue(batch.qoutes);
                        }
                    }

                    @Override
                    public void onFailure(Call<BatchResponse> call, Throwable t) {
                        Log.e(TAG, "Stock batch call failure", t);
                    }
                });

        return stockList;
    }

    public MutableLiveData<ArrayList<String>> getSymbolList(Boolean firstLogin){
        MutableLiveData<ArrayList<String>> symbolList = new MutableLiveData<>();

        if (firstLogin){
            symbolList.setValue(new ArrayList<>(Arrays.asList("YHOO", "AAPL", "GOOG", "MSFT")));
        }else {
            // fetch from shared preferences
            String stockSymbols = mContext.getSharedPreferences("STOCKS", MODE_PRIVATE)
                    .getString(STOCKSYMBOLS, "");
            symbolList.setValue(new ArrayList<>(Arrays.asList(stockSymbols.split(STOCK_DELIMITER_FOR_SP))));
        }

        return symbolList;
    }

    public void fetchSymbolList(String companyName) {
        ApiClient.getSymbolSearchApiInterface().getSymbolForQuery(SYMBOL_SEARCH_API_URL, companyName)
                .enqueue(new Callback<SymbolResponse>() {
            @Override
            public void onResponse(Call<SymbolResponse> call, Response<SymbolResponse> response) {
                if (response.isSuccessful()) {
//                    setSymbolDialogue(response.body().resultSet.result);
                }
            }

            @Override
            public void onFailure(Call<SymbolResponse> call, Throwable t) {

            }
        });
    }
}
