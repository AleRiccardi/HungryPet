package com.aleric.hungrypet._data.shedule;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Schedule {

    /* TABLE */
    public static final String TABLE_NAME = "schedule";
    public static final String _ID = "id";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_WEEK_DAY = "week_day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_DATE_CREATE = "date_create";
    public static final String COLUMN_DATE_UPDATE = "date_update";

    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    static public String[] WEEK_DAYS = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String mId;
    private String mMac;
    private int mWeekDay;
    private int mHour;
    private Date mDateCreate;
    private Date mDateUpdate;


    public Schedule(String mac, int weekDay, int hour) {
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = Calendar.getInstance().getTime();
        mDateUpdate = Calendar.getInstance().getTime();
        mId = generateId(mac, mDateCreate);
    }

    public Schedule(String id, String mac, int weekDay, int hour, Date dateCreate) {
        mId = id;
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = dateCreate;
        mDateUpdate = Calendar.getInstance().getTime();
    }

    public Schedule(String id, String mac, int weekDay, int hour, Date dateCreate, Date dateUpdate) {
        mId = id;
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = dateCreate;
        mDateUpdate = dateUpdate;
    }

    public Schedule(Cursor cursor) {
        this.mId = cursor.getString(cursor.getColumnIndex(_ID));
        this.mMac = cursor.getString(cursor.getColumnIndex(COLUMN_MAC));
        this.mWeekDay = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_WEEK_DAY)));
        this.mHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_HOUR)));

        SimpleDateFormat format = new SimpleDateFormat(this.PATTERN_DATE);
        String curCreate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATE));
        String curUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_UPDATE));

        try {
            this.mDateCreate = format.parse(curCreate);
            this.mDateUpdate = format.parse(curUpdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String generateId(String mac, Date dateCreate) {
        SimpleDateFormat format = new SimpleDateFormat(this.PATTERN_DATE);
        return mac + "-" + format.format(dateCreate);
    }

    public void setWeekDay(int weekDay) {
        mWeekDay = weekDay;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public void setUpdate() {
        this.mDateUpdate = Calendar.getInstance().getTime();
        ;
    }

    public String getId() {
        return mId;
    }

    public String getMac() {
        return mMac;
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
        // Formatting time
        SimpleDateFormat format = new SimpleDateFormat(this.PATTERN_DATE);

        ContentValues cv = new ContentValues();
        cv.put(_ID, mId);
        cv.put(COLUMN_MAC, mMac);
        cv.put(COLUMN_WEEK_DAY, mWeekDay);
        cv.put(COLUMN_HOUR, mHour);
        cv.put(COLUMN_DATE_CREATE, format.format(mDateCreate));
        cv.put(COLUMN_DATE_UPDATE, format.format(mDateCreate));
        return cv;
    }

    public Schedule clone() {
        return new Schedule(mId, mMac, mWeekDay, mHour, mDateCreate);
    }


    public static String createStringHour(int hourAndMinutes) {
        int hour = hourAndMinutes / 100;
        int minutes = hourAndMinutes % 100;
        String hourString = "" + hour + ":" + ((minutes / 10 == 0) ? "0" + minutes : minutes);
        return hourString;
    }
}