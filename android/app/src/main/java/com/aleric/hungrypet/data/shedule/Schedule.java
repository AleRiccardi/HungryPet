package com.aleric.hungrypet.data.shedule;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Schedule {

    public static final String TABLE_NAME = "schedule";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_WEEK_D = "weekD";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_DATE = "date";
    static public String[] WEEK_DAYS = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String mMac;
    private int mWeekDay;
    private int mHour;
    private Date mUpdate;



    public Schedule(String mac, int weekDay, int hour){
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mUpdate = Calendar.getInstance().getTime();
    }

    public Schedule(String mac, int weekDay, int hour ,Date date){
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mUpdate = date;
    }

    public Schedule(Cursor cursor) {
        this.mMac = cursor.getString(cursor.getColumnIndex(COLUMN_MAC));
        this.mWeekDay = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_WEEK_D)));
        this.mHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_HOUR)));

        String s = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.mUpdate = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setMac(String mac){
        mMac = mac;
    }

    public void setWeekDay(int weekDay){
        mWeekDay = weekDay;
    }

    public void setHour(int hour){
        mHour = hour;
    }

    public void setUpdate(Date update){
        mUpdate = update;
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

    public Date getUpdate() {
        return mUpdate;
    }

    /**
     * @// TODO: 16/06/2018 mUpdate is sometime == null, correct that error.
     * @return
     */
    public ContentValues getContentValues() {
        if(mUpdate == null){
            mUpdate = Calendar.getInstance().getTime();
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MAC, mMac);
        cv.put(COLUMN_WEEK_D, mWeekDay);
        cv.put(COLUMN_HOUR, mHour);
        cv.put(COLUMN_DATE, mUpdate.toString());
        return cv;
    }

    public Schedule clone(){
        return new Schedule(mMac,mWeekDay,mHour,mUpdate);
    }


    public static String createStringHour(int hourAndMinutes) {
        int hour = hourAndMinutes / 100;
        int minutes = hourAndMinutes % 100;
        String hourString = "" + hour + ":" + ((minutes / 10 == 0) ? "0" + minutes : minutes);
        return hourString;
    }
}
