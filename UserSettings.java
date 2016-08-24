package com.eztrailermonitor.eztrailermonitor;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * This class reads/writes the user settings via the shared preferences.
 */
public class UserSettings {

    private static SharedPreferences getSharedPrefs(Context context) {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(
                "com.eztrailermonitor.eztrailermonitor.USER_SETTINGS", Context.MODE_PRIVATE);
        return sharedPrefs;
    }

    public static void setStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setBoolValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setLongValue(Context context, String key, long value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getStringValue(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = getSharedPrefs(context);
        return sharedPrefs.getString(key, defaultValue);
    }

    public static int getIntValue(Context context, String key, int defaultValue) {
        SharedPreferences sharedPrefs = getSharedPrefs(context);
        return sharedPrefs.getInt(key, defaultValue);
    }

    public static boolean getBoolValue(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPrefs = getSharedPrefs(context);
        return sharedPrefs.getBoolean(key, defaultValue);
    }

    public static Long getLongValue(Context context, String key, long defaultValue) {
        SharedPreferences sharedPrefs = getSharedPrefs(context);
        return sharedPrefs.getLong(key, defaultValue);
    }
}
