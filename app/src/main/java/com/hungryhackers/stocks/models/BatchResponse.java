package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hungryhackers.stocks.MainActivity;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;

/**
 * Created by YourFather on 14-01-2018.
 */

public class BatchResponse {

    @SerializedName("Stock Quotes")
    @Expose
    public ArrayList<Stock> qoutes;

    private ArrayList<String> allStocks;

    public boolean hasSymbol(String symbol) {
        if (allStocks == null) {
            StringBuilder builder = new StringBuilder();
            for (Stock s :
                    qoutes) {
                builder.append(s.symbol);
                builder.append(MainActivity.STOCK_DELIMITER_FOR_SP);
            }
            // Remove last delimiter with setLength.
            builder.setLength(builder.length() - 1);
            String stockString = builder.toString();
            allStocks = StockUtils.convertToArrayList(stockString, MainActivity.STOCK_DELIMITER_FOR_SP);
        }
        return allStocks.contains(symbol);

    }
}
