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
    public static final String COLUMN_NAME = "nameD";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_UPDATE = "dUpdate";

    private String mMac;
    private String mName;
    private String mIp;
    private Date mUpdate;

    public Station(String mac, String name, String ip) {
        mMac = mac;
        mName = name;
        mIp = ip;
        mUpdate = Calendar.getInstance().getTime();
    }

    public Station(Cursor cursor) {
        this.mMac = cursor.getString(cursor.getColumnIndex(_MAC));
        this.mName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        this.mIp = cursor.getString(cursor.getColumnIndex(COLUMN_IP));

        String s = cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE));
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            this.mUpdate = dateFormat.parse(s);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setMac(String mMac) {
        this.mMac = mMac;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setIp(String mIp) {
        this.mIp = mIp;
    }

    public void setUpdate(Date mUpdate) {
        this.mUpdate = mUpdate;
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

    public Date getUpdate() {
        return mUpdate;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(_MAC, mMac);
        cv.put(COLUMN_NAME, mName);
        cv.put(COLUMN_IP, mIp);
        cv.put(COLUMN_UPDATE, mUpdate.toString());
        return cv;
    }
}