package com.depart.cea.test1.net;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * Created by zhongpeng on 18/3/12.
 */
public class DBHelper {
    public static final String DB_NAME = "db_zhbj";

    public static final String TBNAME_UserInfo = "tb_userinfo";
    public static final String TBNAME_GPSPOSITION = "tb_gpsposition";

    public static final int DB_VERSION = 2;

    private SQLiteDatabase _db;
    private final DBOpenHelper _dbOpenHelper;

    public DBHelper(Context context) {
        _dbOpenHelper = new DBOpenHelper(context, DB_NAME, DB_VERSION);
        if (_db == null) {
            _db = _dbOpenHelper.getWritableDatabase();
        }
    }

    public void CleanUp() {
        if (_db != null) {
            _db.close();
            _db = null;
        }
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String dbName, int version) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String UseInfo_Create = "CREATE TABLE " + TBNAME_UserInfo +
                    "(personid TEXT," +
                    "name TEXT," +
                    "sex TEXT," +
                    "born TEXT," +
                    "mobile TEXT," +
                    "idcard TEXT," +
                    "jobkind TEXT," +
                    "password TEXT," +
                    "epid TEXT," +
                    "groupid TEXT)";

            String GpsPosition_Create = "CREATE TABLE " + TBNAME_GPSPOSITION +
                    " (gtime INTEGER PRIMARY KEY," +
                    "starnum INTEGER," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "speed INTEGER," +
                    "radius INTEGER)";

            try {
                db.execSQL(UseInfo_Create);
                db.execSQL(GpsPosition_Create);
            } catch (SQLException e) {
                Log.d("FOLLOWME", e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                String GpsPosition_Create = "CREATE TABLE " + TBNAME_GPSPOSITION +
                        " (gtime INTEGER PRIMARY KEY," +
                        "starnum INTEGER," +
                        "x INTEGER," +
                        "y INTEGER," +
                        "speed INTEGER," +
                        "radius INTEGER)";
                db.execSQL(GpsPosition_Create);
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }
    }

//    public void UseInfoAdd(UserInfo userInfo) {
//        ContentValues values = new ContentValues();
//        values.put("personid", userInfo.getPersonID());
//        values.put("name", userInfo.getName());
//        values.put("sex", userInfo.getSex());
//        values.put("born", userInfo.getBorn());
//        values.put("mobile", userInfo.getMobile());
//        values.put("idcard", userInfo.getIdCard());
//        values.put("jobkind", userInfo.getJobKind());
//        values.put("password", userInfo.getPassword());
//        values.put("epid", userInfo.getEpID());
//        values.put("groupid", userInfo.getGroupID());
//
//        _db.insert(TBNAME_UserInfo, null, values);
//    }

//    public void UseInfoDelete() {
//        _db.delete(TBNAME_UserInfo, null, null);
//    }
//
//    public void UseInfoGetAll(UserInfo userInfo) {
//        String strSQL = "Select * from " + TBNAME_UserInfo;
//        Cursor cursor = _db.rawQuery(strSQL, null);
//        while (cursor.moveToNext()) {
//            userInfo.setPersonID(cursor.getString(cursor.getColumnIndex("personid")));
//            userInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
//            userInfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
//            userInfo.setBorn(cursor.getString(cursor.getColumnIndex("born")));
//            userInfo.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
//            userInfo.setIdCard(cursor.getString(cursor.getColumnIndex("idcard")));
//            userInfo.setJobKind(cursor.getString(cursor.getColumnIndex("jobkind")));
//            userInfo.setPassword(cursor.getString(cursor.getColumnIndex("password")));
//            userInfo.setEpID(cursor.getString(cursor.getColumnIndex("epid")));
//            userInfo.setGroupID(cursor.getString(cursor.getColumnIndex("groupid")));
//        }
//        cursor.close();
//    }

    public void PositionAdd(GpsPosition position) {
        ContentValues values = new ContentValues();
        values.put("gtime", position.getGtime());
        values.put("starnum", position.getStarnum());
        values.put("x", position.getX());
        values.put("y", position.getY());
        values.put("speed", position.getSpeed());
        values.put("radius", position.getRadius());

        _db.replace(TBNAME_GPSPOSITION, null, values);
    }

    public void PositionDelete(long gtime) {
        _db.delete(TBNAME_GPSPOSITION, "gtime=" + gtime, null);
    }

    public void PositionGetAll(List<GpsPosition> posList) {
        String strSQL = "Select * from " + TBNAME_GPSPOSITION;
        Cursor cursor = _db.rawQuery(strSQL, null);
        while (cursor.moveToNext()) {
            GpsPosition pos = new GpsPosition();
            pos.setGtime(cursor.getLong(cursor.getColumnIndex("gtime")));
            pos.setStarnum(cursor.getInt(cursor.getColumnIndex("starnum")));
            pos.setX(cursor.getInt(cursor.getColumnIndex("x")));
            pos.setY(cursor.getInt(cursor.getColumnIndex("y")));
            pos.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
            pos.setRadius(cursor.getDouble(cursor.getColumnIndex("radius")));

            posList.add(pos);
        }
        cursor.close();
    }

}
