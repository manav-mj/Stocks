package com.hungryhackers.stocks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.hungryhackers.stocks.adapters.StockRecyclerAdapter;
import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.Stock;
import com.hungryhackers.stocks.models.StockSymbol;
import com.hungryhackers.stocks.models.SymbolResponse;
import com.hungryhackers.stocks.network.ApiClient;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hungryhackers.stocks.StringConstants.FIRSTLOGIN;
import static com.hungryhackers.stocks.StringConstants.STOCKSYMBOLS;
import static com.hungryhackers.stocks.StringConstants.SYMBOL_SEARCH_API_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RecyclerView stockRecyclerView;
    StockRecyclerAdapter stockAdapter;

    ArrayList<Stock> stocksList;
    ArrayList<String> stockSymbolArrayList;

    SharedPreferences.Editor editor;

    ProgressDialog stockProgress, symbolProgress;

    Boolean checkFlag = false;

    Animation fabOpen, fabClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stockProgress = new ProgressDialog(this);
        symbolProgress = new ProgressDialog(this);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        stockProgress.setCancelable(false);
        symbolProgress.setCancelable(false);
        stockProgress.setMessage("Fetching Stock Information..");
        symbolProgress.setMessage("Fetching stock symbol..");

        stocksList = new ArrayList<>();
        stockSymbolArrayList = new ArrayList<>();

        stockRecyclerView = findViewById(R.id.stock_recycler_view);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockRecyclerAdapter(this, stocksList);
        stockRecyclerView.setAdapter(stockAdapter);

        final FloatingActionButton fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabAddListener);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabCancel.startAnimation(fabClose);
//                arrayAdapter.notifyDataSetChanged();
                fab.setImageResource(R.drawable.ic_add_black_48dp);
                fab.setOnClickListener(fabAddListener);
                checkFlag = false;
                for (Stock s :
                        stocksList) {
                    s.setChecked(false);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("STOCKS", MODE_PRIVATE);
        editor = sp.edit();
        boolean firstLogin = sp.getBoolean(FIRSTLOGIN, true);
        if (firstLogin) {
            stockSymbolArrayList.addAll(Arrays.asList("YHOO", "AAPL", "GOOG", "MSFT"));
            firstLogin = false;
            editor.putBoolean(FIRSTLOGIN, firstLogin);
            saveStocksToSharedPref();
        } else {
            String stockSymbols = sp.getString(STOCKSYMBOLS, null);
            stockSymbolArrayList.clear();
            stockSymbolArrayList.addAll(Arrays.asList(stockSymbols.split(",")));
        }

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

        StringBuffer stockSymbols = new StringBuffer();
        for (String s : stockSymbolArrayList) {
            stockSymbols.append(s);
            stockSymbols.append(",");
        }
        stockSymbols.deleteCharAt(stockSymbols.length() - 1);
        fetchStockList(stockSymbols, 0);
    }

    void saveStocksToSharedPref() {
        StringBuffer stockSymbols = new StringBuffer();
        for (String s : stockSymbolArrayList) {
            stockSymbols.append(s + ",");
        }
        if (!stockSymbols.toString().isEmpty()) {
            stockSymbols.deleteCharAt(stockSymbols.length() - 1);
        }
        editor.putString(STOCKSYMBOLS, stockSymbols.toString());
        editor.commit();
    }

    private void fetchStockList(StringBuffer stockSymbols, int mode) {
        // Show progress dialogue
        stockProgress.show();

        // Start network call
        ApiClient.getStockSearchApiInterface()
                .getStockBatch(stockSymbols.toString())
                .enqueue(new Callback<BatchResponse>() {
                    @Override
                    public void onResponse(Call<BatchResponse> call, Response<BatchResponse> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Stock batch data successfully fetched");

                            BatchResponse batch = response.body();
                            if (batch == null) {
                                Log.e(TAG, "Stock batch response body null");
                                return;
                            }
                            stocksList.addAll(batch.getStockListFromBatch());
                            stockAdapter.notifyDataSetChanged();

                            // Hide progress dialogue
                            stockProgress.cancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<BatchResponse> call, Throwable t) {
                        Log.e(TAG, "Stock batch call failure", t);
                    }
                });

        saveStocksToSharedPref();
    }

    // On click listener for adding new stock
    View.OnClickListener fabAddListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setTitle("Track new stock");
            View v = getLayoutInflater().inflate(R.layout.dialog_view, null);
            b.setView(v);
            final TextView stockSearchInput = (TextView) v.findViewById(R.id.stock_search_input);
            b.setCancelable(false);
            b.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String companyName = stockSearchInput.getText().toString();
                    fetchSymbolList(companyName);
                }
            });
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog alertDialog = b.create();
            alertDialog.show();
        }
    };

    private void fetchSymbolList(String companyName) {
        // Show progress dialogue
        symbolProgress.show();

        ApiClient.getSymbolSearchApiInterface().getSymbolForQuery(SYMBOL_SEARCH_API_URL, companyName).enqueue(new Callback<SymbolResponse>() {
            @Override
            public void onResponse(Call<SymbolResponse> call, Response<SymbolResponse> response) {
                if (response.isSuccessful()) {
                    setSymbolDialogue(response.body().resultSet.result);

                    // Hide progress dialogue
                    symbolProgress.cancel();
                }
            }

            @Override
            public void onFailure(Call<SymbolResponse> call, Throwable t) {

            }
        });
    }

    private void setSymbolDialogue(final ArrayList<StockSymbol> symbols) {
        if (symbols.size() > 1) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Many results found!!");

            String[] items = new String[symbols.size()];

            for (int i = 0; i < symbols.size(); i++) {
                items[i] = symbols.get(i).getName();
            }
            b.setItems(items, (dialog, which) -> {
                String symbol = symbols.get(which).getSymbol();
                stockSymbolArrayList.add(symbol);
                fetchStockList(new StringBuffer(symbol), 1);
            });
            AlertDialog alert = b.create();
            alert.getListView().setFastScrollEnabled(true);
            alert.getListView().setVerticalScrollBarEnabled(true);
            alert.show();

        } else {
            String symbol = symbols.get(0).getSymbol();
            if (stockSymbolArrayList.toString().contains(symbol)) {
                Toast.makeText(this, "Company already added", Toast.LENGTH_SHORT).show();
                return;
            }

            stockSymbolArrayList.add(symbol);
            fetchStockList(new StringBuffer(symbol), 1);
        }
    }
}
