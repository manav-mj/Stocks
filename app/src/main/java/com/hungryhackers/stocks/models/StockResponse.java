package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by YourFather on 14-01-2018.
 */

public class StockResponse {
    @SerializedName("Meta Data")
    @Expose
    public MetaData metaData;
    @SerializedName("Time Series (1min)")
    @Expose
    public HashMap<String, StockValue> timeSeries1min;

    private class MetaData {
        @SerializedName("1. Information")
        @Expose
        private String information;
        @SerializedName("2. Symbol")
        @Expose
        private String symbol;
        @SerializedName("3. Last Refreshed")
        @Expose
        private String lastRefreshed;
        @SerializedName("4. Interval")
        @Expose
        private String interval;
        @SerializedName("5. Output Size")
        @Expose
        private String outputSize;
        @SerializedName("6. Time Zone")
        @Expose
        private String timeZone;
    }

    private class StockValue {
        @SerializedName("1. open")
        @Expose
        private String open;
        @SerializedName("2. high")
        @Expose
        private String high;
        @SerializedName("3. low")
        @Expose
        private String low;
        @SerializedName("4. close")
        @Expose
        private String close;
        @SerializedName("5. volume")
        @Expose
        private String volume;
    }
}
