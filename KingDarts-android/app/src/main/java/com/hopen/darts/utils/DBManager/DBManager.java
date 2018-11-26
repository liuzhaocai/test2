package com.hopen.darts.utils.DBManager;

import android.content.Context;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by apple on 16/10/26.
 */
public class DBManager {

    protected DBHelper dbHelper;
    protected SQLiteDatabase db;

    /**
     * 单例
     */
    private static DBManager instance;

    public static DBManager getManager(Context context, String dbPath) {
        if (instance == null) {
            instance = new DBManager();
            instance.initDB(context, dbPath);
        }
        return instance;
    }

    /**
     * 初始化db
     *
     * @param context
     */
    public void initDB(Context context, String dbPath) {
        File file = new File(dbPath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(file.getName());//本地
                FileOutputStream fileOutputStream = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                Logger.i("写入成功");
                dbHelper = new DBHelper(context, dbPath);
                db = dbHelper.getWritableDatabase();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.e("数据库创建失败");
            }
        }else {
            dbHelper = new DBHelper(context, dbPath);
            db = dbHelper.getWritableDatabase();
        }
    }

    /**
     * 获取数据库路径
     *
     * @return
     */

    protected String dbPath() {
        return "";
    }

    /**
     * close database
     */
    public void close() {
        db.close();
    }

    /**
     * 公共插入语句
     *
     * @param ts
     * @param pri
     * @param <T>
     */
    public <T> void insert(ArrayList<T> ts, String pri) {
        //开始事务
        db.beginTransaction();
        try {
            //获取类的class
            for (T t : ts) {
                Class cls = t.getClass();
                Field[] fields = cls.getDeclaredFields();//获取所有字段
                String sql = "REPLACE INTO " + cls.getSimpleName() + "(";
                String sqlV = " VALUES(";
                ArrayList<Object> arrayValues = new ArrayList<>();
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i].getName();//获取所有字段名称
                    if (fieldName.equals(pri)) continue;
                    Object filedValue = null;
                    try {
                        int typeInt = fields[i].getModifiers();//获取字段的类型
                        //获取字段的类型申明表，8静态，2私有，16final  =26，类型26为静态常量，不做处理如最终serialVersionUID
                        if (typeInt == 2) {
                            fields[i].setAccessible(true);//设置访问权限
                            filedValue = fields[i].get(t);//获取所有字段的值
                        }else {
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    sql = sql + fieldName + ",";
                    arrayValues.add(filedValue);
                    sqlV = sqlV + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ") ";
                sqlV = sqlV.substring(0, sqlV.length() - 1) + ")";
                Logger.i(sql + sqlV);
                Logger.i(arrayValues.toArray().toString());
                db.execSQL(sql + sqlV, arrayValues.toArray());
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 公共删除
     * @param <T>
     */
    public <T> void delete(Class<T> cls){
        delete(cls,"");
    }

    /**
     * 公共删除
     * @param cls
     * @param condition
     * @param <T>
     */
    public <T> void delete(Class<T> cls,String condition){
        String sql = "delete from " + cls.getSimpleName() + " " + condition;
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 公共查询
     * @param cls
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> queryList(Class<T> cls) {
        return queryList(cls,"");
    }

    /**
     * 公共查询
     * @param cls
     * @param condition
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> queryList(Class<T> cls,String condition) {
        ArrayList<T> list = new ArrayList<>();
        String sql = "select * from " + cls.getSimpleName() + " " + condition;
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i),
                            nullToString(cursor.getString(i)));
                }
                T model = null;
                try {
//                    model = UtilsMapBean.mapToBean(map, cls);
                    model = JSON.parseObject(JSON.toJSONString(map),cls);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(model);
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return list;
    }

    public String nullToString(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string;
    }

}
