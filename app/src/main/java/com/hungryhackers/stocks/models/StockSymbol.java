package com.hungryhackers.stocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by YourFather on 12-03-2017.
 */

public class StockSymbol {
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("exch")
    @Expose
    private String exch;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("exchDisp")
    @Expose
    private String exchDisp;
    @SerializedName("typeDisp")
    @Expose
    private String typeDisp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExch() {
        return exch;
    }

    public void setExch(String exch) {
        this.exch = exch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExchDisp() {
        return exchDisp;
    }

    public void setExchDisp(String exchDisp) {
        this.exchDisp = exchDisp;
    }

    public String getTypeDisp() {
        return typeDisp;
    }

    public void setTypeDisp(String typeDisp) {
        this.typeDisp = typeDisp;
    }
}
