package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by YourFather on 12-03-2017.
 */

public class Stock {
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

    public String name;
    public String change;

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    Boolean isChecked;

    public Stock() {
    }

    public Stock(String symbol) {

        this.symbol = symbol;
    }
}
