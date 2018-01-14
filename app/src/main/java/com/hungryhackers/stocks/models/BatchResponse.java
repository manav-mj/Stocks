package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by YourFather on 14-01-2018.
 */

public class BatchResponse {

    ArrayList<BatchStockValue> qoutes;

    private class BatchStockValue {
        @SerializedName("1. symbol")
        @Expose
        private String symbol;
        @SerializedName("2. price")
        @Expose
        private String price;
        @SerializedName("3. volume")
        @Expose
        private String volume;
        @SerializedName("4. timestamp")
        @Expose
        private String timestamp;
    }
}
