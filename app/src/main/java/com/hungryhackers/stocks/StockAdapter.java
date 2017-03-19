package com.hungryhackers.stocks;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YourFather on 12-03-2017.
 */

public class StockAdapter extends ArrayAdapter<Stock> {
    Context mContext;
    ArrayList<Stock> mStockList;

    public StockAdapter(@NonNull Context context, @NonNull ArrayList<Stock> objects) {
        super(context, 0, objects);
        mContext = context;
        mStockList = objects;
    }

    static class StockViewHolder{
        TextView nameTextView;
        TextView symbolTextView;
        TextView bidTextView;
        TextView changeTextView;

        public StockViewHolder(TextView symbolTextView, TextView bidTextView, TextView changeTextView) {
//            this.nameTextView = nameTextView;
            this.symbolTextView = symbolTextView;
            this.bidTextView = bidTextView;
            this.changeTextView = changeTextView;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.adapter_card_view, null);

//            TextView nameTextView = (TextView) convertView.findViewById(R.id.company_name);
            TextView symbolTextView = (TextView) convertView.findViewById(R.id.company_symbol);
            TextView bidTextView = (TextView) convertView.findViewById(R.id.company_bid);
            final TextView changeTextView = (TextView) convertView.findViewById(R.id.company_change);

            StockViewHolder stockViewHolder = new StockViewHolder(symbolTextView, bidTextView, changeTextView);
            convertView.setTag(stockViewHolder);
        }
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.card_item_layout);
        layout.setBackgroundColor(Color.WHITE);
        StockViewHolder stockViewHolder = (StockViewHolder) convertView.getTag();
        Stock stock = mStockList.get(position);
        //stockViewHolder.nameTextView.setText(stock.name);
        stockViewHolder.bidTextView.setText(stock.bid);
        stockViewHolder.changeTextView.setText(stock.change);
        stockViewHolder.symbolTextView.setText(stock.symbol);
        if (stock.change.charAt(0) == '+'){
            stockViewHolder.changeTextView.setTextColor(Color.rgb(00,153,00));
        }else{
            stockViewHolder.changeTextView.setTextColor(Color.RED);
        }
        convertView.setPadding(0,0,0,0);
        if (position == mStockList.size()-1){
            convertView.setPadding(0,0,0,130);
        }

        return convertView;
    }
}
