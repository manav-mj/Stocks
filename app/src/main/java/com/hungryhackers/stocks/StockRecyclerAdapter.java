package com.hungryhackers.stocks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hungryhackers.stocks.models.Stock;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YourFather on 14-01-2018.
 */

public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.StockViewHolder>{

    Context mContext;
    ArrayList<Stock> mStockList;

    public StockRecyclerAdapter(Context mContext, ArrayList<Stock> mStockList) {
        this.mContext = mContext;
        this.mStockList = mStockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_card_view, parent, false);
        return new StockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = mStockList.get(position);
        holder.bidTextView.setText(stock.bid);
        holder.symbolTextView.setText(stock.symbol);
//        holder.changeTextView.setText(stock.change);
//        if (stock.change.charAt(0) == '+'){
//            holder.changeTextView.setTextColor(Color.rgb(00,153,00));
//        }else{
//            holder.changeTextView.setTextColor(Color.RED);
//        }
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.company_symbol)
        TextView symbolTextView;
        @BindView(R.id.company_bid)
        TextView bidTextView;
        @BindView(R.id.company_change)
        TextView changeTextView;

        public StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
