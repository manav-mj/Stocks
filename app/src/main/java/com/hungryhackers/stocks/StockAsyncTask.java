package com.hungryhackers.stocks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by YourFather on 12-03-2017.
 */

public class StockAsyncTask extends AsyncTask<String, Void, ArrayList<Stock>> {

    StockDownloadListener listener;
    int mMode;

    void setStockDownloadListener(StockDownloadListener listener, int mode){

        this.listener = listener;
        mMode = mode;
    }

    @Override
    protected ArrayList<Stock> doInBackground(String... params) {
        String urlString = params[0];
        StringBuffer stringBuffer = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null){
                return null;
            }

            Scanner s = new Scanner(inputStream);
            while (s.hasNext()){
                stringBuffer.append(s.nextLine());
            }

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return parseStockList(stringBuffer.toString());
    }

    private ArrayList<Stock> parseStockList(String json) {
        try {
            JSONObject object = new JSONObject(json);
            ArrayList<Stock> stockList = new ArrayList<>();
            if (mMode == 0) {
                JSONArray stockArray = object.getJSONObject("query").getJSONObject("results").getJSONArray("quote");
                for (int i = 0 ; i < stockArray.length() ; i++){
                    JSONObject stockObject = stockArray.getJSONObject(i);
                    String symbol = stockObject.getString("symbol");
                    String bid = stockObject.getString("BookValue");
                    String change = stockObject.getString("Change");
                    Stock s = new Stock(symbol, bid, change);
                    stockList.add(s);
                }
            } else if (mMode == 1) {
                JSONObject stockObject = object.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
                String symbol = stockObject.getString("symbol");
                String bid = stockObject.getString("BookValue");
                String change = stockObject.getString("Change");
                Stock s = new Stock(symbol, bid, change);
                stockList.add(s);
            }

            return stockList;
        } catch (JSONException e) {
            JSONObject object = null;
            try {
                object = new JSONObject(json);
                ArrayList<Stock> stockList = new ArrayList<>();
                Log.i("manav", "inside this");
                JSONObject stockObject = object.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
                String symbol = stockObject.getString("symbol");
                String bid = stockObject.getString("BookValue");
                String change = stockObject.getString("Change");
                Stock s = new Stock(symbol, bid, change);
                stockList.add(s);
                return stockList;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Stock> stocks) {
        super.onPostExecute(stocks);
        if (listener != null){
            listener.onStockDownloadComplete(stocks, mMode);
        }
    }

    public interface StockDownloadListener {
        void onStockDownloadComplete(ArrayList<Stock> stocks, int mode);
    }
}
