package com.aleric.hungrypet._data.wifi;

public class WifiDirectory {
    public static WifiDirectory INSTANCE = null;
    private WifiCell mWifi = null;
    private WifiDirectory(){}

    public static WifiDirectory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WifiDirectory();
        }
        return INSTANCE;
    }

    public void setWifi(WifiCell wifi){
        mWifi = wifi;
    }

    public WifiCell getWifi() {
        return mWifi;
    }

}
