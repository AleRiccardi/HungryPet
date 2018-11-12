package com.aleric.hungrypet._data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aleric.hungrypet._data.shedule.Schedule;

import java.util.ArrayList;
import java.util.List;

public class DbScheduleManager {
    private DbHelper dbHelper;


    public DbScheduleManager(Context context) {
        dbHelper = new DbHelper(context);
    }


    public boolean addSchedule(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = 0;
        try {
            row = db.insert(Schedule.TABLE_NAME, null, schedule.getContentValues());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public boolean updateSchedule(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = 0;
        try {
            row = db.update(Schedule.TABLE_NAME, schedule.getContentValues(),
                    Schedule._ID + " = ? ", new String[]{schedule.getId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public boolean addScheduleWithCheck(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Schedule mySchedule = null;
        Boolean success = true;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + Schedule.TABLE_NAME +
                    " WHERE " + Schedule.COLUMN_WEEK_DAY + " = '" + schedule.getWeekDay() + "' and " +
                    Schedule.COLUMN_HOUR + " = '" + schedule.getHour() + "' and " +
                    Schedule.COLUMN_MAC + " = '" + schedule.getMac() + "' ";
            cursor = db.rawQuery(query, null);
            if (cursor.moveToNext()) {
                mySchedule = new Schedule(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        if (mySchedule != null) {
            mySchedule.setWeekDay(schedule.getWeekDay());
            mySchedule.setHour(schedule.getHour());
            mySchedule.setDelete(false);
            mySchedule.setUpdate();
            success = this.updateSchedule(mySchedule);
        } else {
            success = this.addSchedule(schedule);
        }

        return success;
    }

    public boolean deleteSchedule(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.delete(Schedule.TABLE_NAME,
                Schedule._ID + " = ? ", new String[]{schedule.getId()});
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
