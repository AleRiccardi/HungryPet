package com.aleric.hungrypet._data.station;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aleric on 05/04/2018.
 */

public class Station {

    public static final String TABLE_NAME = "station";
    public static final String _MAC = "mac";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_DATE_CEATE = "date_create";
    public static final String COLUMN_DATE_UPDATE = "date_update";

    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat FORMAT_DATA = new SimpleDateFormat(PATTERN_DATE);

    private String mMac;
    private String mName;
    private String mIp;
    private Date mDateCreate;
    private Date mDateUpdate;

    public Station(String mac, String name, String ip) {
        mMac = mac;
        mName = name;
        mIp = ip;
        mDateCreate = Calendar.getInstance().getTime();
        mDateUpdate = Calendar.getInstance().getTime();
    }

    public Station(Cursor cursor) {
        mMac = cursor.getString(cursor.getColumnIndex(_MAC));
        mName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        mIp = cursor.getString(cursor.getColumnIndex(COLUMN_IP));

        String curCreate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CEATE));
        String curUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CEATE));

        try {
            mDateCreate = FORMAT_DATA.parse(curCreate);
            mDateUpdate = FORMAT_DATA.parse(curUpdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setMac(String mac) {
        mMac = mac;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public void setDateUpdate(Date dateUpdate) {
        mDateUpdate = dateUpdate;
    }

    public String getMac() {
        return mMac;
    }

    public String getName() {
        return mName;
    }

    public String getIp() {
        return mIp;
    }

    public Date getDateCreate() {
        return mDateCreate;
    }

    public Date getDateUpdate() {
        return mDateUpdate;
    }

    public ContentValues getContentValues() {
        // Formatting time

        ContentValues cv = new ContentValues();
        cv.put(_MAC, mMac);
        cv.put(COLUMN_IP, mIp);
        cv.put(COLUMN_NAME, mName);
        cv.put(COLUMN_DATE_CEATE, FORMAT_DATA.format(mDateCreate));
        cv.put(COLUMN_DATE_UPDATE, FORMAT_DATA.format(mDateCreate));
        return cv;
    }
}