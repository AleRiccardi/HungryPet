package com.aleric.hungrypet.data;

public class DeviceDirectory {
    public static DeviceDirectory INSTANCE = null;

    private Device mDevice = null;

    private DeviceDirectory(){}

    public static DeviceDirectory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DeviceDirectory();
        }
        return INSTANCE;
    }

    public void setDevice(Device device){
        mDevice = device;
    }

    public Device getDevice() {
        return mDevice;
    }

}
