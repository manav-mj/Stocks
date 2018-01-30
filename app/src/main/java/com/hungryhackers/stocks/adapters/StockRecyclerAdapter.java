package com.hungryhackers.stocks.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hungryhackers.stocks.R;
import com.hungryhackers.stocks.models.Stock;
import com.hungryhackers.stocks.models.StockViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YourFather on 14-01-2018.
 */

public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.StockViewHolder>{

    Context mContext;
    StockViewModel stockViewModel;

    public StockRecyclerAdapter(Context mContext, StockViewModel stockViewModel) {
        this.mContext = mContext;
        this.stockViewModel = stockViewModel;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_stock_list, parent, false);
        return new StockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockViewModel.getStockList().getValue().get(position);
        holder.bidTextView.setText(stock.price);
        holder.symbolTextView.setText(stock.symbol);
        if (stock.price == null){
            holder.changeLayout.setBackground(null);
            holder.changeTextView.setVisibility(View.GONE);
            holder.changeSign.setImageResource(R.drawable.ic_remove_white_48dp);
            holder.percentTextView.setVisibility(View.GONE);

        }else {
            holder.changeLayout.setBackgroundResource(R.drawable.stock_item_change_bg);
            holder.changeTextView.setVisibility(View.VISIBLE);
            holder.changeSign.setImageResource(R.drawable.ic_add_white_48dp);
            holder.percentTextView.setVisibility(View.VISIBLE);
        }
//        holder.changeTextView.setText(stock.change);
//        if (stock.change.charAt(0) == '+'){
//            holder.changeTextView.setTextColor(Color.rgb(00,153,00));
//        }else{
//            holder.changeTextView.setTextColor(Color.RED);
//        }
    }

    @Override
    public int getItemCount() {
        if (!stockViewModel.dataIsNull())
            return stockViewModel.getStockList().getValue().size();
        return 0;
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.company_symbol)
        TextView symbolTextView;
        @BindView(R.id.company_bid)
        TextView bidTextView;
        @BindView(R.id.company_change)
        TextView changeTextView;
        @BindView(R.id.change_layout)
        LinearLayout changeLayout;
        @BindView(R.id.change_sign)
        ImageView changeSign;
        @BindView(R.id.company_change_percentage_symbol)
        TextView percentTextView;

        public StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
