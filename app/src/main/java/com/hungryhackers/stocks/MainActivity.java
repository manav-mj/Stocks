package com.hungryhackers.stocks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hungryhackers.stocks.models.BatchResponse;
import com.hungryhackers.stocks.models.StockResponse;
import com.hungryhackers.stocks.network.ApiClient;

import org.json.JSONArray;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hungryhackers.stocks.StringConstants.API_KEY;
import static com.hungryhackers.stocks.StringConstants.FIRSTLOGIN;
import static com.hungryhackers.stocks.StringConstants.STOCKSYMBOLS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView stockListView;

    ArrayList<Stock> stocksList;
    ArrayList<String> stockNames;

    ArrayList<String> stockSymbolArrayList;
    Set<String> stockSymbolSet;

    StockAdapter arrayAdapter;
//    ArrayAdapter arrayAdapter;

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

        stockSymbolSet = new HashSet<>();

        stockProgress = new ProgressDialog(this);
        symbolProgress = new ProgressDialog(this);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        stockProgress.setCancelable(false);
        symbolProgress.setCancelable(false);
        stockProgress.setMessage("Fetching Stock Information..");
        symbolProgress.setMessage("Fetching stock symbol..");

        stockNames = new ArrayList<>();
        stocksList = new ArrayList<>();
        stockSymbolArrayList = new ArrayList<>();

        stockListView = (ListView) findViewById(R.id.stock_list_view);
//        stockAdapter = new StockAdapter(this, stocksList);
//        stockListView.setAdapter(stockAdapter);
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stockNames);
        arrayAdapter = new StockAdapter(this, stocksList);
        stockListView.setAdapter(arrayAdapter);
        stockListView.setDivider(null);

        final FloatingActionButton fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabAddListener);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabCancel.startAnimation(fabClose);
                arrayAdapter.notifyDataSetChanged();
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

        stockListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                checkFlag = true;

                stocksList.get(position).setChecked(true);
                fab.setImageResource(R.drawable.ic_delete_black_48dp);
                fabCancel.startAnimation(fabOpen);
                RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
                layout.setBackgroundColor(Color.LTGRAY);
                View.OnClickListener fabDeleteListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int pos = 0; pos < stocksList.size(); pos++) {
                            Log.i("manav", pos + " : " + stocksList.get(pos).isChecked);
                            Log.e("manav", pos + "");
                            if (stocksList.get(pos).isChecked) {
                                stockSymbolArrayList.remove(pos);
                                stockNames.remove(pos);
                                stocksList.remove(pos);
                                pos--;
                            }

                        }
                        if (stockSymbolArrayList.size() > 0) {
                            String firstString = stockSymbolArrayList.get(0);
                            if (firstString.contains("%2C")) {
                                firstString = firstString.substring(3);
                                stockSymbolArrayList.remove(0);
                                stockSymbolArrayList.add(0, firstString);
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                        saveStocksToSharedPref();

                        Toast.makeText(MainActivity.this, "Stock Removed", Toast.LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.ic_add_black_48dp);
                        fab.setOnClickListener(fabAddListener);
                        fabCancel.startAnimation(fabClose);
                        checkFlag = false;
                    }
                };

                fab.setOnClickListener(fabDeleteListener);
                return true;
            }
        });

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkFlag) {
                    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
                    if (stocksList.get(position).isChecked) {
                        layout.setBackgroundColor(Color.WHITE);
                        stocksList.get(position).setChecked(false);
                    } else {
                        layout.setBackgroundColor(Color.LTGRAY);
                        stocksList.get(position).setChecked(true);
                    }
                    Log.e("manav", position + " : " + stocksList.get(position).isChecked);
                }
            }
        });

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

    private void fetchSymbolList(String companyName) {
        symbolProgress.show();
    }


    private void fetchStockList(StringBuffer stockSymbols, int mode) {
        // Show progress dialogue
        stockProgress.show();

        // Start network call
        ApiClient.getApiInterface()
                .getStockBatch(stockSymbols.toString())
                .enqueue(new Callback<BatchResponse>() {
                    @Override
                    public void onResponse(Call<BatchResponse> call, Response<BatchResponse> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Stock batch data successfully fetched");

                            BatchResponse batch = response.body();

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
}
