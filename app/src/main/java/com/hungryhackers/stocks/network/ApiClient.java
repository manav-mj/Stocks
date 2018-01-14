package com.hungryhackers.stocks.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YourFather on 27-12-2017.
 */

public class ApiClient {
    private static ApiInterface apiInterface;

    public static ApiInterface getApiInterface(){

        if (apiInterface == null){
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.alphavantage.co/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().addInterceptor(new StockInterceptor()).build())
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);
        }

        return apiInterface;
    }

    private static class StockInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("apikey", "XM1MYNHOPM814AD5")
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }
}
