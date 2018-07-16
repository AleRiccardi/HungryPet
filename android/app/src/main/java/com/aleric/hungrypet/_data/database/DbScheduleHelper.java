package com.aleric.hungrypet._data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aleric.hungrypet._data.shedule.Schedule;

public class DbScheduleHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_hungrypet.db";
    private static final int DATABASE_VERSION = 17;

    public DbScheduleHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE " +
            Schedule.TABLE_NAME + " (" +
            Schedule._ID + " VARCHAR(40) NOT NULL, " +
            Schedule.COLUMN_MAC + " VARCHAR(20) NOT NULL, " +
            Schedule.COLUMN_WEEK_DAY + " INTEGER NOT NULL, " +
            Schedule.COLUMN_HOUR + " INTEGER NOT NULL, " +
            Schedule.COLUMN_DATE_CREATE + " DATETIME NOT NULL, " +
            Schedule.COLUMN_DATE_UPDATE + " DATETIME NOT NULL, " +
            Schedule.COLUMN_DELETED + " BIT NOT NULL, " +
            "PRIMARY KEY (" + Schedule._ID + "), " +
            "CONSTRAINT UC_Schedule UNIQUE (" + Schedule.COLUMN_MAC + ", " + Schedule.COLUMN_WEEK_DAY + ", " + Schedule.COLUMN_HOUR + "), " +
            "FOREIGN KEY (" + Schedule.COLUMN_MAC + ") REFERENCES station(mac)" +
            ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbScheduleHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Schedule.TABLE_NAME);
        onCreate(db);
    }
}
