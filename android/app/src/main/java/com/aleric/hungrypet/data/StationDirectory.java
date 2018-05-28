package com.aleric.hungrypet.data;

public class StationDirectory {
    public static StationDirectory INSTANCE = null;

    private Station mStation = null;

    private StationDirectory(){}

    public static StationDirectory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new StationDirectory();
        }
        return INSTANCE;
    }

    public void setStation(Station station){
        mStation = station;
    }

    public Station getStation() {
        return mStation;
    }

}
