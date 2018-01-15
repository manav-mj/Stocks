package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by YourFather on 15-01-2018.
 */

public class SymbolResponse {

    @SerializedName("ResultSet")
    @Expose
    public SymbolResultSet resultSet;

    public class SymbolResultSet {
        @SerializedName("Result")
        @Expose
        public ArrayList<StockSymbol> result;
    }
}
