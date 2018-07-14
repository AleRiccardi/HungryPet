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
    public static final String _ID = "id";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_DATE_CEATE = "date_create";
    public static final String COLUMN_DATE_UPDATE = "date_update";

    private int mId;
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
        this.mId = cursor.getInt(cursor.getColumnIndex(_ID));
        this.mMac = cursor.getString(cursor.getColumnIndex(COLUMN_MAC));
        this.mName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        this.mIp = cursor.getString(cursor.getColumnIndex(COLUMN_IP));

        String curCreate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CEATE));
        SimpleDateFormat dateFormatCreate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

        String curUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CEATE));
        SimpleDateFormat dateFormatUpdate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

        try {
            this.mDateCreate = dateFormatCreate.parse(curCreate);
            this.mDateUpdate = dateFormatUpdate.parse(curUpdate);
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

    public void setDateUpdate(Date dateUpdate) {
        this.mDateUpdate = dateUpdate;
    }

    public int getId() {
        return mId;
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
        ContentValues cv = new ContentValues();
        cv.put(_ID, mId);
        cv.put(COLUMN_MAC, mMac);
        cv.put(COLUMN_NAME, mName);
        cv.put(COLUMN_DATE_CEATE, mDateCreate.toString());
        cv.put(COLUMN_DATE_UPDATE, mDateUpdate.toString());
        return cv;
    }
}