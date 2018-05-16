package com.aleric.hungrypet.data;

public class WifiDirectory {
    public static WifiDirectory INSTANCE = null;
    private WifiCell mWifi = null;
    private CommService mComm = null;

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

    public void setComm(CommService comm){
        mComm = comm;
    }

    public WifiCell getWifi() {
        return mWifi;
    }

    public CommService getComm() {
        return mComm;
    }
}
