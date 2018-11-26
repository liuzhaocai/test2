package com.hopen.darts.utils.DBManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * Created by apple on 2016/12/5.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;

    public DBHelper(Context context, String dbPath){
        super(context, dbPath, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i("xinye", "#############数据库创建了##############:" + DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.i("xinye", "#############数据库升级了##############:" + DB_VERSION);
    }

}

