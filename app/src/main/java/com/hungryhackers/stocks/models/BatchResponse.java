package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by YourFather on 14-01-2018.
 */

public class BatchResponse {

    @SerializedName("Stock Quotes")
    @Expose
    ArrayList<BatchStockValue> qoutes;

    public class BatchStockValue {
        @SerializedName("1. symbol")
        @Expose
        public String symbol;
        @SerializedName("2. price")
        @Expose
        public String price;
        @SerializedName("3. volume")
        @Expose
        public String volume;
        @SerializedName("4. timestamp")
        @Expose
        public String timestamp;
    }

    public ArrayList<Stock> getStockListFromBatch(){
        ArrayList<Stock> stocks = new ArrayList<>();
        for (BatchStockValue batchStockValue:
             qoutes) {
            Stock stock = new Stock(batchStockValue.symbol, batchStockValue.price, batchStockValue.volume);
            stocks.add(stock);
        }
        return stocks;
    }
}
