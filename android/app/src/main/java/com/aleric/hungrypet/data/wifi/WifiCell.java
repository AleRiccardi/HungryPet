package com.aleric.hungrypet.data.wifi;

public class WifiCell {
    private String ssid;
    private String encryption;
    private String pswd;

    public WifiCell(String ssid, String encryption){
        this.ssid = ssid;
        this.encryption = encryption;
        this.pswd = null;
    }

    public void setPassword(String pswd){
        this.pswd = pswd;
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
