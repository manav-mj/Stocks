package com.hungryhackers.stocks;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YourFather on 12-03-2017.
 */

public class StockOptionsAdapter extends ArrayAdapter<StockSymbol> {
    Context mContext;
    ArrayList<StockSymbol> mStockSymbols;
    public StockOptionsAdapter(@NonNull Context context, @NonNull ArrayList<StockSymbol> objects) {
        super(context, 0,  objects);
        mContext = context;
        mStockSymbols = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = View.inflate(mContext, android.R.layout.simple_list_item_2, null);
        TextView name = (TextView) v.findViewById(android.R.id.text1);
        TextView symbol = (TextView) v.findViewById(android.R.id.text2);
        name.setText(mStockSymbols.get(position).companyName);
        symbol.setText(mStockSymbols.get(position).companySymbol);
        return v;
    }
}
