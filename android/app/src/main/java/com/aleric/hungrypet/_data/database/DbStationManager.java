package com.aleric.hungrypet._data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aleric.hungrypet._data.station.Station;

import java.util.ArrayList;
import java.util.List;

public class DbStationManager {

    private DbHelper dbHelper;


    public DbStationManager(Context context) {
        dbHelper = new DbHelper(context);
    }


    public boolean addStation(Station station) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(Station.TABLE_NAME, null, station.getContentValues());
        return row > 0;
    }

    public boolean updateStation(Station station) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.update(Station.TABLE_NAME, station.getContentValues(),
                Station._MAC + " = ? ", new String[]{String.valueOf(station.getMac())});
        return row > 0;
    }

    public boolean deleteStation(Station station) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.delete(Station.TABLE_NAME,
                Station._MAC + " = ? ", new String[]{String.valueOf(station.getMac())});
        return row > 0;
    }

    public List<Station> getStations() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Station> stations = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Station.TABLE_NAME +
                    " ORDER BY " + Station.COLUMN_NAME + " ASC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Station person = new Station(cursor);
                stations.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return stations;
    }

}
