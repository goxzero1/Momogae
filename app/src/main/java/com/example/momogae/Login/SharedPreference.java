package com.example.momogae.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//값 저장
public class SharedPreference {
public static void setAttribute(Context context, String key, String value){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(key, value);
    editor.commit();
}

// 값 읽기
public static String getAttribute(Context context, String key){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(key, null);
}

}
