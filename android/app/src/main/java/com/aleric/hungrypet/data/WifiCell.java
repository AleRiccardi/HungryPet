package com.aleric.hungrypet.data;

public class WifiCell {
    private String ssid;
    private String pswd;

    public WifiCell(String ssid){
        this.ssid = ssid;
        this.pswd = null;
    }

    @Override
    public String toString() {
        return ssid;
    }
}
