package com.aleric.hungrypet.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elia.dipasquale on 16/03/2017.
 */

/**
 * Per dettagli vedere: db-example.
 */
public class DbManager {

    private DbHelper dbHelper;


    public DbManager(Context context) {
        dbHelper = new DbHelper(context);
    }

/*
    public boolean addPerson(Person person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(Person.TABLE_NAME, null, person.getContentValues());
        return row > 0;
    }

    public boolean updatePerson(Person person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.update(Person.TABLE_NAME, person.getContentValues(),
                Person._ID + " = ? ", new String[]{String.valueOf(person.getId())});
        return row > 0;
    }

    public boolean deletePerson(Person person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.delete(Person.TABLE_NAME,
                Person._ID + " = ? ", new String[]{String.valueOf(person.getId())});
        return row > 0;
    }

    public List<Person> getPeople() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Person> people = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Person.TABLE_NAME +
                    " ORDER BY " + Person.COLUMN_NAME + " ASC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Person person = new Person(cursor);
                people.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return people;
    }
    */
}
