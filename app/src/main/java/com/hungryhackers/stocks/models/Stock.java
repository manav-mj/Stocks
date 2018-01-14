package com.hungryhackers.stocks.models;

/**
 * Created by YourFather on 12-03-2017.
 */

public class Stock {
    public String name;
    public String symbol;
    public String bid;
    public String change;

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    Boolean isChecked;

    public Stock(String symbol, String bid, String change) {
        //this.name = name;
        this.symbol = symbol;
        this.bid = bid;
        this.change = change;
        isChecked = false;
    }
}
