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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import static com.hungryhackers.stocks.StringConstants.API_KEY;
import static com.hungryhackers.stocks.StringConstants.FIRSTLOGIN;
import static com.hungryhackers.stocks.StringConstants.STOCKSYMBOLS;

public class MainActivity extends AppCompatActivity implements StockAsyncTask.StockDownloadListener, SymbolAsyncTask.SymbolDownloadListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stockSymbolSet = new HashSet<>();

        stockProgress = new ProgressDialog(this);
        symbolProgress = new ProgressDialog(this);

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


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabAddListener);

        SharedPreferences sp = getSharedPreferences("STOCKS", MODE_PRIVATE);
        editor = sp.edit();
        boolean firstLogin = sp.getBoolean(FIRSTLOGIN,true);
        if (firstLogin){
            stockSymbolArrayList.addAll(Arrays.asList("%22YHOO%22","%2C%22AAPL%22","%2C%22GOOG%22","%2C%22MSFT%22"));
            firstLogin = false;
            editor.putBoolean(FIRSTLOGIN, firstLogin);
            saveStocksToSharedPref();
        }else {
            String stockSymbols = sp.getString(STOCKSYMBOLS, null);
            stockSymbolArrayList.clear();
            stockSymbolArrayList.addAll(Arrays.asList(stockSymbols.split(",")));
        }
        if (!stockSymbolArrayList.get(0).isEmpty()) {
            for (int i = 0; i < stockSymbolArrayList.size(); i++) {
                String[] s = stockSymbolArrayList.get(i).split("%22");
                stockSymbolSet.add(s[1]);
            }
        }

        stockListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                checkFlag = true;

                stocksList.get(position).setChecked(true);
                Log.e("manav",position + " : " + stocksList.get(position).isChecked);
                fab.setImageResource(R.drawable.ic_delete_black_48dp);
                RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
                layout.setBackgroundColor(Color.LTGRAY);
                View.OnClickListener fabDeleteListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int pos = 0 ; pos<stocksList.size() ; pos++) {
                            Log.i("manav",pos + " : " + stocksList.get(pos).isChecked);
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
                if (checkFlag){
                    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.card_item_layout);
                    if (stocksList.get(position).isChecked){
                        layout.setBackgroundColor(Color.WHITE);
                        stocksList.get(position).setChecked(false);
                    }else {
                        layout.setBackgroundColor(Color.LTGRAY);
                        stocksList.get(position).setChecked(true);
                    }
                    Log.e("manav",position + " : " + stocksList.get(position).isChecked);
                }
            }
        });

        StringBuffer stockSymbols = new StringBuffer();
        for (String s : stockSymbolArrayList) {
            stockSymbols.append(s);
        }
        fetchStockList(stockSymbols, 0);
    }

    void saveStocksToSharedPref(){
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
        String urlString = "http://stocksearchapi.com/api/?search_text=" + companyName + "&api_key=" + API_KEY;
        SymbolAsyncTask task = new SymbolAsyncTask();
        task.setSymbolDownloadListener(this);
        task.execute(urlString);
    }


    private void fetchStockList(StringBuffer stockSymbols, int mode) {
        stockProgress.show();

        String urlString = "https://query.yahooapis.com/v1/public/yql" + "?" + "q=select%20symbol%2CChange%2CBookValue%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(" +
                stockSymbols + ")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        StockAsyncTask task = new StockAsyncTask();
        task.setStockDownloadListener(this, mode);
        task.execute(urlString);
        saveStocksToSharedPref();
    }



    @Override
    public void onStockDownloadComplete(ArrayList<Stock> stocks, int mode) {
        stockProgress.cancel();
        if (stocks == null){
            return;
        }
        if (mode == 0){
            stocksList.clear();
            stocksList.addAll(stocks);
            stockNames.clear();
            for (Stock s : stocks) {
                stockNames.add(s.symbol);
            }
        }else if (mode == 1){
            stocksList.addAll(stocks);
            stockNames.add(stocks.get(0).symbol);
            Toast.makeText(this, "Stock Added", Toast.LENGTH_SHORT).show();
        }

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSymbolDownloadComplete(final ArrayList<StockSymbol> symbols) {
        symbolProgress.cancel();
        if (symbols == null){
            Toast.makeText(this, "No company matching your search found", Toast.LENGTH_SHORT).show();
        }else {
            ArrayList<String> names = new ArrayList<>();
            for (int i=0 ; i<symbols.size() ; i++){
                if (stockSymbolSet.contains(symbols.get(i).companySymbol)){
                    symbols.remove(i);
                    i--;
                }else {
                    names.add(symbols.get(i).companyName);
                }
            }

            if (symbols.size() > 1){
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Many results found!!");

                String[] items = names.toArray(new String[names.size()]);
                b.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer symbolToUrlFormat = new StringBuffer();
                        symbolToUrlFormat.append("%22" + symbols.get(which).companySymbol + "%22");
                        stockSymbolSet.add(symbols.get(which).companySymbol);


                        if (stockSymbolArrayList.isEmpty()){
                            stockSymbolArrayList.add(symbolToUrlFormat.toString());
                        }else {
                            String s = "%2C" + symbolToUrlFormat.toString();
                            //symbolToUrlFormat.append("%22" + symbols.get(which).companySymbol + "%22");
                            stockSymbolArrayList.add(s);
                        }
                        fetchStockList(symbolToUrlFormat, 1);

                    }
                });
                AlertDialog alert = b.create();
                alert.getListView().setFastScrollEnabled(true);
                alert.getListView().setVerticalScrollBarEnabled(true);
                alert.show();
            }else {
                if (stockSymbolArrayList.toString().contains(symbols.get(0).companySymbol)){
                    Toast.makeText(this, "Company already added", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer symbolToUrlFormat = new StringBuffer();
                symbolToUrlFormat.append("%22" + symbols.get(0).companySymbol + "%22");

                if (stockSymbolArrayList.isEmpty()){
                    stockSymbolArrayList.add(symbolToUrlFormat.toString());
                }else {
                    String s = "%2C" + symbolToUrlFormat.toString();
                    //symbolToUrlFormat.append("%22" + symbols.get(which).companySymbol + "%22");
                    stockSymbolArrayList.add(s);
                }
                fetchStockList(symbolToUrlFormat, 1);
            }

        }
    }

    View.OnClickListener fabAddListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setTitle("Track new stock");
            View v = getLayoutInflater().inflate(R.layout.dialog_view,null);
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
