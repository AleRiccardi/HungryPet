package com.aleric.hungrypet._data.wifi;

public class WifiCell {
    private String mSsid;
    private String mPswd;

    public WifiCell(String ssid){
        mSsid = ssid;
        mPswd = null;
    }

    public void setPassword(String pswd){
        mPswd = pswd;
    }

    public String getSsid(){
        return mSsid;
    }

    public String getPswd(){
        return mPswd;
    }

}
