package com.aleric.hungrypet._data.wifi;

public class WifiCell {
    private String mSsid;
    private String mEncryption;
    private String mPswd;

    public WifiCell(String ssid, String encryption){
        mSsid = ssid;
        mEncryption = encryption;
        mPswd = null;
    }

    public void setPassword(String pswd){
        mPswd = pswd;
    }

    public String getSsid(){
        return mSsid;
    }

    public String getEncryption(){
        return mEncryption;
    }

    public String getPswd(){
        return mPswd;
    }

}
