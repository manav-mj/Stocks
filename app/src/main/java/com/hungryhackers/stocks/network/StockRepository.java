package com.hungryhackers.stocks.network;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.hungryhackers.stocks.MainActivity;
import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.Stock;
import com.hungryhackers.stocks.models.StockSymbol;
import com.hungryhackers.stocks.models.SymbolResponse;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.hungryhackers.stocks.MainActivity.STOCK_DELIMITER_FOR_SP;
import static com.hungryhackers.stocks.StringConstants.SP_STOCK_SYMBOL_KEY;
import static com.hungryhackers.stocks.StringConstants.SYMBOL_SEARCH_API_URL;

/**
 * Created by YourFather on 22-01-2018.
 */

public class StockRepository {
    private static final String TAG = "StockRepository";

    private Context mContext;
    private static MutableLiveData<ArrayList<Stock>> stockList;
    private static MutableLiveData<ArrayList<String>> symbolList;

    public StockRepository(Context mContext) {
        this.mContext = mContext;
        if (stockList == null && symbolList == null) {
            stockList = new MutableLiveData<>();
            symbolList = new MutableLiveData<>();
        }
    }

    public MutableLiveData<ArrayList<Stock>> getStockList(String stockSymbols) {
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
                                stockList.setValue(batch.quotes);

                                // Check for empty stocks
                                ArrayList<String> symbols = StockUtils.convertToArrayList(stockSymbols, MainActivity.STOCK_DELIMITER_FOR_SP);
                                if (symbols.size() > batch.quotes.size()) {
                                    for (String s : symbols) {
                                        if (!batch.hasSymbol(s)) {
                                            batch.quotes.add(new Stock(s));
                                        }
                                    }
                                }
                            } else {
                                if (batch.quotes.size() == 0) {
                                    batch.quotes.add(new Stock(stockSymbols));
                                    Log.i(TAG, "No stock found for symbol : " + stockSymbols + " : Empty stock added");
                                }
                                stockList.getValue().addAll(batch.quotes);
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

        if (firstLogin) {
            symbolList.setValue(new ArrayList<>(Arrays.asList("YHOO", "AAPL", "GOOG", "MSFT")));
        } else {
            // fetch from shared preferences
            String stockSymbols = mContext.getSharedPreferences("STOCKS", MODE_PRIVATE)
                    .getString(SP_STOCK_SYMBOL_KEY, null);

            if (stockSymbols == null) {
                getSymbolList(true);
            } else {
                symbolList.setValue(new ArrayList<>(Arrays.asList(stockSymbols.split(STOCK_DELIMITER_FOR_SP))));
            }
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
