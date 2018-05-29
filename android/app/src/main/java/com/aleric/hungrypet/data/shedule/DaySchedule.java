package com.aleric.hungrypet.data.shedule;

public class DaySchedule {
    private String mDayName;
    private int mNumSchedule;

    public DaySchedule(String dayName, int numSchedule){
        mDayName = dayName;
        mNumSchedule = numSchedule;
    }

    public String getDayName(){
        return mDayName;
    }

    public int getNumSchedule(){
        return mNumSchedule;
    }

}
