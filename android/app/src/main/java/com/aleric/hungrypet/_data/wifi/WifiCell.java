package com.aleric.hungrypet._data.wifi;

public class WifiCell {
    private String ssid;
    private String encryption;
    private String pswd;

    public WifiCell(String ssid, String encryption){
        ssid = ssid;
        encryption = encryption;
        pswd = null;
    }

    public void setPassword(String pswd){
        pswd = pswd;
    }

    public String getSsid(){
        return ssid;
    }

    public String getEncryption(){
        return encryption;
    }

    public String getPswd(){
        return pswd;
    }

}
