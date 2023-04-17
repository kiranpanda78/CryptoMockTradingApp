package com.example.stocktradingapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppUtil {

    SharedPreferences sharedPreferences;

    public final static String Key_Watchlist = "Watchlist";
    public final static String Key_Positions = "Positions";

    public SharedPreferences getSharedPreferences(Activity activity) {
        sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}
