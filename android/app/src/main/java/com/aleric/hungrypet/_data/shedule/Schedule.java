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
    public static final String COLUMN_DELETED = "deleted";

    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat FORMAT_DATA = new SimpleDateFormat(PATTERN_DATE);
    static public String[] WEEK_DAYS = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


    private String mId;
    private String mMac;
    private int mWeekDay;
    private int mHour;
    private Date mDateCreate;
    private Date mDateUpdate;
    private int mDeleted = 0;


    public Schedule(String mac, int weekDay, int hour) {
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = Calendar.getInstance().getTime();
        mDateUpdate = Calendar.getInstance().getTime();
        mId = generateId(mac, mDateCreate);
    }

    public Schedule(String id, String mac, int weekDay, int hour, Date dateCreate, int deleted) {
        mId = id;
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = dateCreate;
        mDateUpdate = Calendar.getInstance().getTime();
        mDeleted = deleted;
    }

    public Schedule(String id, String mac, int weekDay, int hour, Date dateCreate, Date dateUpdate, int deleted) {
        mId = id;
        mMac = mac;
        mWeekDay = weekDay;
        mHour = hour;
        mDateCreate = dateCreate;
        mDateUpdate = dateUpdate;
        mDeleted = deleted;
    }

    public Schedule(Cursor cursor) {
        mId = cursor.getString(cursor.getColumnIndex(_ID));
        mMac = cursor.getString(cursor.getColumnIndex(COLUMN_MAC));
        mWeekDay = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_WEEK_DAY)));
        mHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_HOUR)));
        mDeleted = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_DELETED)));
        String curCreate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATE));
        String curUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_UPDATE));

        try {
            mDateCreate = FORMAT_DATA.parse(curCreate);
            mDateUpdate = FORMAT_DATA.parse(curUpdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String generateId(String mac, Date dateCreate) {
        return mac + "-" + FORMAT_DATA.format(dateCreate);
    }

    public void setWeekDay(int weekDay) {
        mWeekDay = weekDay;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public void setUpdate() {
        mDateUpdate = Calendar.getInstance().getTime();
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
        return mDateCreate;
    }

    public Date getDateUpdate() {
        return mDateUpdate;
    }

    public String getDateCreateToString() {
        return FORMAT_DATA.format(mDateCreate);
    }

    public String getDateUpdateToString() {
        return FORMAT_DATA.format(mDateUpdate);
    }

    public int getDeleted(){
        return mDeleted;
    }


    public ContentValues getContentValues() {
        // Formatting time
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_DATE);

        ContentValues cv = new ContentValues();
        cv.put(_ID, mId);
        cv.put(COLUMN_MAC, mMac);
        cv.put(COLUMN_WEEK_DAY, mWeekDay);
        cv.put(COLUMN_HOUR, mHour);
        cv.put(COLUMN_DATE_CREATE, format.format(mDateCreate));
        cv.put(COLUMN_DATE_UPDATE, format.format(mDateUpdate));
        cv.put(COLUMN_DELETED, mDeleted);
        return cv;
    }

    public void setDelete(boolean delete) {
        mDeleted = delete ? 1 : 0;
    }

    public boolean isAvailable() {
        return mDeleted == 0;
    }

    public static String createStringHour(int hourAndMinutes) {
        int hour = hourAndMinutes / 100;
        int minutes = hourAndMinutes % 100;
        return hour + ":" + ((minutes / 10 == 0) ? "0" + minutes : minutes);
    }

    @Override
    public Schedule clone() {
        return new Schedule(mId, mMac, mWeekDay, mHour, mDateCreate, mDeleted);
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this){
            return true;
        }

        /* Check if o is an instance of Schedule or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Schedule)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Schedule c = (Schedule) o;

        // Compare the data members and return accordingly
        return mId.equals(c.mId);
    }
}