package com.aleric.hungrypet.data.communication;

/**
 */
public interface CommConstants {

    // Message types sent from the CommService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_CONNECTED = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_FAILED = 6;
    public static final int CONNECTION_LOST = 7;


    // Key names received from the CommService Handler
    public static final String DEVICE_NAME = "Nexus5X";
    public static final String TOAST = "toast";

}
