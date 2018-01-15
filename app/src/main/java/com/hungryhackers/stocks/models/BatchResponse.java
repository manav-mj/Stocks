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
    public ArrayList<Stock> qoutes;
}
