package com.hungryhackers.stocks.network;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.Stock;
import com.hungryhackers.stocks.models.StockSymbol;
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

    public MutableLiveData<ArrayList<Stock>> getStockList(String stockSymbols, MutableLiveData<ArrayList<Stock>> existingStockList) {
        MutableLiveData<ArrayList<Stock>> stockList;
        if (existingStockList == null)
            stockList = new MutableLiveData<>();
        else
            stockList = existingStockList;


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
                            if (stockList.getValue() == null) {
                                stockList.setValue(batch.qoutes);
                            }else {
                                if (batch.qoutes.size() == 0) {
                                    Stock emptyStock = new Stock();
                                    emptyStock.symbol = call.request().url().queryParameter("symbols");
                                    batch.qoutes.add(emptyStock);
                                    Log.i(TAG, "No stock found for symbol : " + emptyStock.symbol + " : Empty stock added");
                                }
                                stockList.getValue().addAll(batch.qoutes);
                                stockList.setValue(stockList.getValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BatchResponse> call, Throwable t) {
                        Log.e(TAG, "Stock batch call failure", t);
                    }
                });

        return stockList;
    }

    public MutableLiveData<ArrayList<String>> getSymbolList(Boolean firstLogin) {
        MutableLiveData<ArrayList<String>> symbolList = new MutableLiveData<>();

        if (firstLogin) {
            symbolList.setValue(new ArrayList<>(Arrays.asList("YHOO", "AAPL", "GOOG", "MSFT")));
        } else {
            // fetch from shared preferences
            String stockSymbols = mContext.getSharedPreferences("STOCKS", MODE_PRIVATE)
                    .getString(STOCKSYMBOLS, null);
            symbolList.setValue(new ArrayList<>(Arrays.asList(stockSymbols.split(STOCK_DELIMITER_FOR_SP))));
        }

        return symbolList;
    }

    public MutableLiveData<ArrayList<StockSymbol>> getSymbolsForName(String companyName) {
        MutableLiveData<ArrayList<StockSymbol>> symbolSearchResponse = new MutableLiveData<>();

        ApiClient.getSymbolSearchApiInterface().getSymbolForQuery(SYMBOL_SEARCH_API_URL, companyName)
                .enqueue(new Callback<SymbolResponse>() {
                    @Override
                    public void onResponse(Call<SymbolResponse> call, Response<SymbolResponse> response) {
                        if (response.isSuccessful()) {
                            symbolSearchResponse.setValue(response.body().resultSet.result);
                        }
                    }

                    @Override
                    public void onFailure(Call<SymbolResponse> call, Throwable t) {

                    }
                });

        return symbolSearchResponse;
    }
}
