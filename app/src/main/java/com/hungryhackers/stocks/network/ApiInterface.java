package com.hungryhackers.stocks.network;

import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.StockResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by YourFather on 27-12-2017.
 */

public interface ApiInterface {

    @GET("query?function=TIME_SERIES_INTRADAY&interval=1min")
    Call<StockResponse> getStockValue(@Query("symbol") String symbol);

    @GET("query?function=BATCH_STOCK_QUOTES")
    Call<BatchResponse> getStockBatch(@Query("symbols") String... symbols);

}
