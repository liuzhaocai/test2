package com.hopen.darts.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.C;


/**
 * Created by wangtiansoft_flavin on 16/9/28.
 */

public class SharePreUtil {
    private SharePreUtil() {
        init(BaseApplication.getContext());
    }

    private SharedPreferences mPres;
    private static SharePreUtil mInstance;

    public void init(Context context) {
        mPres = context.getSharedPreferences(C.app.sp_name,
                Context.MODE_PRIVATE);
    }

    public void clear() {
        SharedPreferences.Editor edit = mPres.edit();
        edit.clear();
        edit.commit();
    }

    public static SharePreUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SharePreUtil();
        }
        return mInstance;
    }

    public String getString(String key, String defValue) {
        return mPres.getString(key, defValue);
    }

    public String getString(String key) {
        return mPres.getString(key, "");
    }

    public int getInt(String key) {
        return mPres.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        try {
            return mPres.getInt(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defValue;
        }
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor edit = mPres.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public long getLong(String key) {
        return mPres.getLong(key, -1);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = mPres.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor edit = mPres.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = mPres.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean getBoolean(String key, boolean value) {
        return mPres.getBoolean(key, value);
    }

    public boolean getBoolean(String key) {
        return mPres.getBoolean(key, false);
    }
}
