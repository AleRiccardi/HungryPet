package com.aleric.hungrypet._data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aleric.hungrypet._data.station.Station;

public class DbStationHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_hungrypet.db";
    private static final int DATABASE_VERSION = 2;

    public DbStationHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLE_STATION = "CREATE TABLE "
            + Station.TABLE_NAME + " (" +
            Station._MAC + " VARCHAR(12) NOT NULL, " +
            Station.COLUMN_NAME + " VARCHAR(30), " +
            Station.COLUMN_IP + " VARCHAR(12) NOT NULL, " +
            Station.COLUMN_UPDATE + " DATETIME, " +
            " PRIMARY KEY (mac) " +
            ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
