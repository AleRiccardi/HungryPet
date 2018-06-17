package com.aleric.hungrypet._data.shedule;

import java.util.List;

public class ScheduleDirectory {

    public static ScheduleDirectory INSTANCE = null;

    private List<Schedule> mSchedule = null;

    private ScheduleDirectory(){

    }

    public static ScheduleDirectory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ScheduleDirectory();
        }
        return INSTANCE;
    }

}
