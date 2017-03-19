package com.hungryhackers.stocks;

import android.os.AsyncTask;

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

public class SymbolAsyncTask extends AsyncTask<String, Void, ArrayList<StockSymbol>>{

    SymbolDownloadListener listener;

    void setSymbolDownloadListener(SymbolDownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<StockSymbol> doInBackground(String... params) {
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
        return parseSymbolList(stringBuffer.toString());
    }

    private ArrayList<StockSymbol> parseSymbolList(String json) {
        try {
            JSONArray symbolArray = new JSONArray(json);
            ArrayList<StockSymbol> symbolList = new ArrayList<>();
            for (int i = 0 ; i < symbolArray.length() ; i++){
                JSONObject symbolObject = symbolArray.getJSONObject(i);
                String symbol = symbolObject.getString("company_symbol");
                String name = symbolObject.getString("company_name");
                StockSymbol stockSymbol = new StockSymbol(name, symbol);
                symbolList.add(stockSymbol);
            }
            return symbolList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<StockSymbol> symbols) {
        super.onPostExecute(symbols);
        if (listener != null){
            listener.onSymbolDownloadComplete(symbols);
        }
    }

    public interface SymbolDownloadListener {
        void onSymbolDownloadComplete(ArrayList<StockSymbol> symbols);
    }
}
