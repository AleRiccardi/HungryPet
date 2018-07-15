package com.aleric.hungrypet._data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aleric.hungrypet._data.shedule.Schedule;

import java.util.ArrayList;
import java.util.List;

public class DbScheduleManager {
    private DbScheduleHelper dbHelper;


    public DbScheduleManager(Context context) {
        dbHelper = new DbScheduleHelper(context);
    }


    public boolean addSchedule(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(Schedule.TABLE_NAME, null, schedule.getContentValues());
        return row > 0;
    }

    public boolean updateSchedule(Schedule scheduleOld, Schedule scheduleNew) {
        String macS = String.valueOf(scheduleOld.getMac());
        String weekDS = String.valueOf(scheduleOld.getWeekDay());
        String hourS = String.valueOf(scheduleOld.getHour());
        String selection =
                Schedule.COLUMN_MAC + " = ? AND " +
                        Schedule.COLUMN_WEEK_DAY + " = ? AND " +
                        Schedule.COLUMN_HOUR + " = ? ";
        String[] selectionArg = new String[]{macS, weekDS, hourS};
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.update(
                Schedule.TABLE_NAME,
                scheduleNew.getContentValues(),
                selection,
                selectionArg
        );
        return row > 0;
    }

    public boolean deleteSchedule(Schedule schedule) {
        String macS = String.valueOf(schedule.getMac());
        String weekDS = String.valueOf(schedule.getWeekDay());
        String hourS = String.valueOf(schedule.getHour());
        String selection =
                Schedule.COLUMN_MAC + " = ? AND " +
                        Schedule.COLUMN_WEEK_DAY + " = ? AND " +
                        Schedule.COLUMN_HOUR + " = ? ";
        String[] selectionArg = new String[]{macS, weekDS, hourS};
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long row = db.delete(
                Schedule.TABLE_NAME,
                selection,
                selectionArg
        );

        return row > 0;
    }


    public List<Schedule> getSchedules(String mac) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Schedule> schedules = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Schedule.TABLE_NAME +
                    " WHERE mac = '" + mac + "'" +
                    " ORDER BY " + Schedule.COLUMN_WEEK_DAY + " ASC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Schedule schedule = new Schedule(cursor);
                schedules.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return schedules;
    }
}
