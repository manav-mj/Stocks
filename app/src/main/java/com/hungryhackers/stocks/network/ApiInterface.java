package com.hungryhackers.stocks.network;

import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.StockResponse;
import com.hungryhackers.stocks.models.SymbolResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryName;
import retrofit2.http.Url;

/**
 * Created by YourFather on 27-12-2017.
 */

public interface ApiInterface {

    @GET("query?function=TIME_SERIES_INTRADAY&interval=1min")
    Call<StockResponse> getStockValue(@Query("symbol") String symbol);

    @GET("query?function=BATCH_STOCK_QUOTES")
    Call<BatchResponse> getStockBatch(@Query("symbols") String... symbols);

    @GET
    Call<SymbolResponse> getSymbolForQuery(@Url String url, @Query("query") String query);
}
