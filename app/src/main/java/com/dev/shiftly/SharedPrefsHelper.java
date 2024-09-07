package com.dev.shiftly;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefsHelper {

  private final String MY_PREFS = "MY_PREFS";
  private final String LOW_END_WARNING = "LOW_END_WARNING";



    private SharedPreferences sharedPreferences;
    private static SharedPrefsHelper prefsHelper;

    private SharedPrefsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
    }

    public static SharedPrefsHelper getInstance(Context context) {
        if (prefsHelper == null) {
            prefsHelper = new SharedPrefsHelper(context);
        }
        return prefsHelper;
    }

    public boolean getLOW_END_WARNING() {
        return sharedPreferences.getBoolean(LOW_END_WARNING, false);
    }

    public void setLOW_END_WARNING(boolean low_end_warning) {
        sharedPreferences.edit().putBoolean(LOW_END_WARNING, low_end_warning).apply();
    }
  public void PutString(String key,String value) {
     sharedPreferences.edit().putString(key, value).apply();
  }

  public String getString(String key) {
    return sharedPreferences.getString(key,null);
  }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
