package com.aleric.hungrypet.WifiWrap;

public class Wifi {
    private String ssid;
    private String encryption;
    private String pswd;

    public Wifi(String ssid, String type){
        this.ssid = ssid;
        this.encryption = type;
        this.pswd = null;
    }

    @Override
    public String toString() {
        return "ssid: " + ssid + ", encryption: " + encryption;
    }
}
