package com.aleric.hungrypet.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aleric.hungrypet.data.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elia.dipasquale on 16/03/2017.
 */

/**
 * Per dettagli vedere: db-example.
 */
public class DbDeviceManager {

    private DbDeviceHelper dbHelper;


    public DbDeviceManager(Context context) {
        dbHelper = new DbDeviceHelper(context);
    }


    public boolean addDevice(Device person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(Device.TABLE_NAME, null, person.getContentValues());
        return row > 0;
    }

    public boolean updateDevice(Device person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.update(Device.TABLE_NAME, person.getContentValues(),
                Device._MAC + " = ? ", new String[]{String.valueOf(person.getMac())});
        return row > 0;
    }

    public boolean deleteDevice(Device person) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.delete(Device.TABLE_NAME,
                Device._MAC + " = ? ", new String[]{String.valueOf(person.getMac())});
        return row > 0;
    }

    public List<Device> getDevices() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Device> devices = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Device.TABLE_NAME +
                    " ORDER BY " + Device.COLUMN_NAME + " ASC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Device person = new Device(cursor);
                devices.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return devices;
    }

}
