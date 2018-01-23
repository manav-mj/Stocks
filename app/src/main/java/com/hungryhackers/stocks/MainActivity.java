package com.hungryhackers.stocks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.hungryhackers.stocks.adapters.StockRecyclerAdapter;
import com.hungryhackers.stocks.models.StockSymbol;
import com.hungryhackers.stocks.models.StockViewModel;
import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hungryhackers.stocks.StringConstants.FIRSTLOGIN;
import static com.hungryhackers.stocks.StringConstants.STOCKSYMBOLS;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String STOCK_DELIMITER_FOR_SP = ",";

    @BindView(R.id.fab_cancel)
    FloatingActionButton fabCancel;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.stock_recycler_view)
    RecyclerView stockRecyclerView;

    private StockRecyclerAdapter stockAdapter;

    private StockViewModel stockViewModel;

    private SharedPreferences.Editor editor;

    private Boolean checkFlag = false;

    private Animation fabOpen, fabClose;

    private StockRepository stockRepository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        stockViewModel = new StockViewModel();

        fab.setOnClickListener(fabAddListener);

//        fabCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fabCancel.startAnimation(fabClose);
////                arrayAdapter.notifyDataSetChanged();
//                fab.setImageResource(R.drawable.ic_add_black_48dp);
//                fab.setOnClickListener(fabAddListener);
//                checkFlag = false;
//                for (Stock s :
//                        stocksList) {
//                    s.setChecked(false);
//                }
//            }
//        });

        SharedPreferences sp = getSharedPreferences("STOCKS", MODE_PRIVATE);
        editor = sp.edit();
        boolean firstLogin = sp.getBoolean(FIRSTLOGIN, true);
        stockRepository = new StockRepository(this);
        stockViewModel.init(firstLogin, stockRepository);
        if (firstLogin) {
            firstLogin = false;
            editor.putBoolean(FIRSTLOGIN, firstLogin);
        }

        stockViewModel.getSymbolList().observe(this, symbolList -> {
            saveStocksToSharedPref(StockUtils.convertToString(symbolList));
        });

        stockViewModel.getStockList().observe(this, stockList -> {
            stockAdapter.notifyDataSetChanged();
        });

        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockRecyclerAdapter(this, stockViewModel);
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
    }

    void saveStocksToSharedPref(String symbolListString) {
        editor.putString(STOCKSYMBOLS, symbolListString);
        editor.commit();
    }

    // On click listener for adding new stock
    View.OnClickListener fabAddListener = view -> {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle("Track new stock");
        View v = getLayoutInflater().inflate(R.layout.dialog_view, null);
        b.setView(v);
        final TextView stockSearchInput = v.findViewById(R.id.stock_search_input);
        b.setCancelable(false);
        b.setPositiveButton("Add", (dialog, which) -> {
            String companyName = stockSearchInput.getText().toString();
            stockViewModel.fetchSymbolForName(companyName);
            stockViewModel.getSymbolSearchResponse().observe(this, this::setSymbolDialogue);
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            return;
        });
        AlertDialog alertDialog = b.create();
        alertDialog.show();
    };

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
                stockViewModel.addStockWithSymbol(symbol);
            });
            AlertDialog alert = b.create();
            alert.getListView().setFastScrollEnabled(true);
            alert.getListView().setVerticalScrollBarEnabled(true);
            alert.show();

        } else {
            String symbol = symbols.get(0).getSymbol();
//            if (stockSymbolArrayList.toString().contains(symbol)) {
//                Toast.makeText(this, "Company already added", Toast.LENGTH_SHORT).show();
//                return;
//            }

            stockViewModel.addStockWithSymbol(symbol);
        }
    }
}
