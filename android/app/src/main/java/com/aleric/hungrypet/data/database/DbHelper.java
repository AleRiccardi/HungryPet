package com.aleric.hungrypet.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by elia.dipasquale on 16/03/2017.
 */

/**
 * Per dettagli vedere: db-example.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercis.db";
    private static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

/*
    public static final String CREATE_TABLE_PEOPLE = "CREATE TABLE "
            + Person.TABLE_NAME + " (" +
            Person._ID + " INTEGER PRIMARY KEY, " +
            Person.COLUMN_NAME + " TEXT, " +
            Person.COLUMN_SURNAME + " TEXT, " +
            Person.COLUMN_AGE + " INTEGER, " +
            Person.COLUMN_EMAIL + " TEXT )";

*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATE_TABLE_PEOPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
