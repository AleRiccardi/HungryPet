package com.aleric.hungrypet.data;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

import com.aleric.hungrypet.BaseView;

public class CommDirectory {

    public static final String A_WIFI_GET = "wifi-get";
    public static final String A_WIFI_SET = "wifi-set";
    public static final String A_BT_DISCONNECT = "bt-quit";

    public static final int E_NONE = 0;
    public static final int E_BT_NOT_SUPPORTED = 1;
    public static final int E_BT_OFF = 2;
    public static final int E_BT_ERROR = 3;
    /**
     * Stati instance of the class
     */
    private static CommDirectory instance;
    /**
     * Member object for the chat services
     */
    private static CommService mComm = null;

    public CommDirectory() {
    }

    public static CommDirectory getInstance(){
        if(instance == null) {
            instance = new CommDirectory();
        }
        return instance;
    }

    public int setComm(Handler handler) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            closeComm();
            return E_BT_NOT_SUPPORTED;
        } else if (!bluetoothAdapter.isEnabled()) {
            closeComm();
            return E_BT_OFF;
        } else {
            if (mComm == null) {
                // Create and start the communication services
                mComm = new CommService(handler);
                mComm.start();
            } else if (mComm.getState() == CommService.STATE_NONE) {
                // Start the Bluetooth chat services
                mComm.start();
            }
            return E_NONE;
        }
    }

    public void setHandler(Handler handler) {
        mComm.setHandler(handler);
    }

    public boolean restartComm() {
        if (mComm == null) {
            return false;
        } else if (mComm.getState() == CommService.STATE_NONE) {
            // Start the communication services
            mComm.start();
            return true;
        } else {
            return (mComm.getState() == CommService.STATE_CONNECTED);
        }
    }


    /**
     * Close the communication.
     */
    public boolean closeComm() {
        if (mComm != null) {
            mComm.stop();
            mComm = null;
        }
        return true;
    }

    /**
     * Sends a message.
     *
     * @param msg A string of text to send.
     */
    public boolean sendMessage(String msg) {
        // Check that we're actually connected before trying anything.
        if (mComm == null) {
            return false;
        } else if (mComm.getState() != CommService.STATE_CONNECTED) {
            return false;
        } else if (msg.length() > 0) {
            // Check that there's actually something to send.
            // Get the message bytes and tell the CommService to write.
            byte[] send = msg.getBytes();
            mComm.write(send);
            return true;
        }
        return false;
    }

    public int getState(){
        if (mComm != null) {
            return mComm.getState();
        } else {
            return CommService.STATE_NONE;
        }
    }
}
