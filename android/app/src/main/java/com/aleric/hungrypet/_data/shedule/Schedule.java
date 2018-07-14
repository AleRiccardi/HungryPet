package com.aleric.hungrypet._data.shedule;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Schedule {

    public static final String TABLE_NAME = "schedule";
    public static final String _ID = "id";
    public static final String COLUMN_WEEK_DAY = "week_day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_DATE_CREATE = "date_create";
    public static final String COLUMN_DATE_UPDATE = "date_update";
    public static final String _ID_STATION = "id_station";
    static public String[] WEEK_DAYS = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private int mId;
    private int mWeekDay;
    private int mHour;
    private Date mDateCreate;
    private Date mDateUpdate;
    private int mIdStation;


    public Schedule(int weekDay, int hour, int idStation) {
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = Calendar.getInstance().getTime();
        mDateUpdate = Calendar.getInstance().getTime();
        mIdStation = idStation;
    }

    public Schedule(int id, int weekDay, int hour, Date dateCreate, int idStation) {
        mId = id;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = dateCreate;
        mDateUpdate = Calendar.getInstance().getTime();
        mIdStation = idStation;
    }

    public Schedule(Cursor cursor) {
        this.mId = cursor.getInt(cursor.getColumnIndex(_ID));
        this.mWeekDay = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_WEEK_DAY)));
        this.mHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_HOUR)));
        this.mIdStation = cursor.getInt(cursor.getColumnIndex(_ID_STATION));

        String curCreate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATE));
        SimpleDateFormat dateFormatCreate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

        String curUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_UPDATE));
        SimpleDateFormat dateFormatUpdate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

        try {
            this.mDateCreate = dateFormatCreate.parse(curCreate);
            this.mDateUpdate = dateFormatUpdate.parse(curUpdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setWeekDay(int weekDay) {
        mWeekDay = weekDay;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public void setUpdate() {
        this.mDateUpdate = Calendar.getInstance().getTime();;
    }

    public int getId() {
        return mId;
    }

    public int getWeekDay() {
        return mWeekDay;
    }

    public int getHour() {
        return mHour;
    }

    public Date getDateCreate() {
        return this.mDateCreate;
    }

    public Date getmDateUpdate() {
        return this.mDateUpdate;
    }


    public ContentValues getContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(_ID, mId);
        cv.put(COLUMN_WEEK_DAY, mWeekDay);
        cv.put(COLUMN_HOUR, mHour);
        cv.put(COLUMN_DATE_CREATE, mDateCreate.toString());
        cv.put(COLUMN_DATE_UPDATE, mDateUpdate.toString());
        return cv;
    }

    public Schedule clone() {
        return new Schedule(mId, mWeekDay, mHour, mDateCreate, mIdStation);
    }


    public static String createStringHour(int hourAndMinutes) {
        int hour = hourAndMinutes / 100;
        int minutes = hourAndMinutes % 100;
        String hourString = "" + hour + ":" + ((minutes / 10 == 0) ? "0" + minutes : minutes);
        return hourString;
    }
}