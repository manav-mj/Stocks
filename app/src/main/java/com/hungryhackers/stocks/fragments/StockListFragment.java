package com.hungryhackers.stocks.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungryhackers.stocks.R;
import com.hungryhackers.stocks.adapters.StockRecyclerAdapter;
import com.hungryhackers.stocks.models.StockViewModel;
import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.hungryhackers.stocks.StringConstants.FIRST_LOGIN;
import static com.hungryhackers.stocks.StringConstants.SP_STOCK_SYMBOL_KEY;

public class StockListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.stock_recycler_view)
    RecyclerView stockRecyclerView;

    private StockRecyclerAdapter stockAdapter;

    private StockViewModel stockViewModel;

    private SharedPreferences.Editor editor;

    private StockRepository stockRepository;

    public StockListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stockViewModel = new StockViewModel();

        SharedPreferences sp = getActivity().getSharedPreferences("STOCKS", MODE_PRIVATE);
        editor = sp.edit();
        boolean firstLogin = sp.getBoolean(FIRST_LOGIN, true);
        stockRepository = new StockRepository(getContext());
        stockViewModel.init(firstLogin, stockRepository);
        if (firstLogin) {
            firstLogin = false;
            editor.putBoolean(FIRST_LOGIN, firstLogin);
        }

        stockViewModel.getSymbolList().observe(this, symbolList -> {
            saveStocksToSharedPref(StockUtils.convertToString(symbolList));
        });

        stockViewModel.getStockList().observe(this, stockList -> {
            stockAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_list, container, false);
        ButterKnife.bind(this, view);

        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stockAdapter = new StockRecyclerAdapter(getContext(), stockViewModel);
        stockRecyclerView.setAdapter(stockAdapter);

//        stockRecyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                checkFlag = true;
//
//                stocksList.get(position).setChecked(true);
//                fab.setImageResource(R.drawable.ic_delete_black_48dp);
//                fabCancel.startAnimation(fabOpen);
//                RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
//                layout.setBackgroundColor(Color.LTGRAY);
//                View.OnClickListener fabDeleteListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        for (int pos = 0; pos < stocksList.size(); pos++) {
//                            Log.i("manav", pos + " : " + stocksList.get(pos).isChecked);
//                            Log.e("manav", pos + "");
//                            if (stocksList.get(pos).isChecked) {
//                                stockSymbolArrayList.remove(pos);
//                                stockNames.remove(pos);
//                                stocksList.remove(pos);
//                                pos--;
//                            }
//
//                        }
//                        if (stockSymbolArrayList.size() > 0) {
//                            String firstString = stockSymbolArrayList.get(0);
//                            if (firstString.contains("%2C")) {
//                                firstString = firstString.substring(3);
//                                stockSymbolArrayList.remove(0);
//                                stockSymbolArrayList.add(0, firstString);
//                            }
//                        }
//                        arrayAdapter.notifyDataSetChanged();
//                        saveStocksToSharedPref();
//
//                        Toast.makeText(MainActivity.this, "Stock Removed", Toast.LENGTH_SHORT).show();
//                        fab.setImageResource(R.drawable.ic_add_black_48dp);
//                        fab.setOnClickListener(fabAddListener);
//                        fabCancel.startAnimation(fabClose);
//                        checkFlag = false;
//                    }
//                };
//
//                fab.setOnClickListener(fabDeleteListener);
//                return true;
//            }
//        });
//
//        stockRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (checkFlag) {
//                    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
//                    if (stocksList.get(position).isChecked) {
//                        layout.setBackgroundColor(Color.WHITE);
//                        stocksList.get(position).setChecked(false);
//                    } else {
//                        layout.setBackgroundColor(Color.LTGRAY);
//                        stocksList.get(position).setChecked(true);
//                    }
//                    Log.e("manav", position + " : " + stocksList.get(position).isChecked);
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnSearchItemClickListener) {
//            mListener = (OnSearchItemClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnSearchItemClickListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void saveStocksToSharedPref(String symbolListString) {
        editor.putString(SP_STOCK_SYMBOL_KEY, symbolListString);
        editor.commit();
    }
}
