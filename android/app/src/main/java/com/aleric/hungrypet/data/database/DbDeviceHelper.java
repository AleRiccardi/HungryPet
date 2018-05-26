package com.aleric.hungrypet.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aleric.hungrypet.data.Device;

/**
 * Created by elia.dipasquale on 16/03/2017.
 */

/**
 * Per dettagli vedere: db-example.
 */
public class DbDeviceHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercis.db";
    private static final int DATABASE_VERSION = 1;


    public DbDeviceHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static final String CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS "
            + Device.TABLE_NAME + " (" +
            Device._MAC + " VARCHAR(12) NOT NULL, " +
            Device.COLUMN_NAME + " VARCHAR(30), " +
            Device.COLUMN_IP + " VARCHAR(12) NOT NULL, " +
            Device.COLUMN_UPDATE + " DATETIME, " +
            " PRIMARY KEY (mac) " +
            ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
