package com.hungryhackers.stocks.utils;

import com.hungryhackers.stocks.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by YourFather on 23-01-2018.
 */

public class StockUtils {

    public static String convertToString(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        // Append all Strings in ArrayList to the StringBuilder.
        for (String string : list) {
            builder.append(string);
            builder.append(MainActivity.STOCK_DELIMITER_FOR_SP);
        }
        // Remove last delimiter with setLength.
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public static ArrayList<String> convertToArrayList(String string, String delimiter){
        return new ArrayList<>(Arrays.asList(string.split(delimiter)));
    }
}
