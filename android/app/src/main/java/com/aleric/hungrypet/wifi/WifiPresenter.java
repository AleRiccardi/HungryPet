package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet._data.communication.CommDirectory;
import com.aleric.hungrypet._data.communication.CommService;
import com.aleric.hungrypet._data.communication.CommConstants;
import com.aleric.hungrypet._data.wifi.WifiCell;
import com.aleric.hungrypet._data.wifi.WifiDirectory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WifiPresenter implements WifiContract.Presenter {

    private static final String TAG = "wifi-mPresenter";

    private WifiContract.View mView;
    /**
     * Communication service instance
     */
    private CommDirectory mComm;
    /**
     * Set if it's the first time that we start the activity
     */
    private boolean mIsNewActivity;

    static String messageReed = "";


    public WifiPresenter(@NonNull WifiContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mComm = CommDirectory.getInstance();
    }

    @Override
    public void start() {
        if (mComm.getState() == CommService.STATE_NONE) {
            startCommunication();
        } else if (mComm.getState() == CommService.STATE_CONNECTED) {
            mComm.setHandler(mHandler);
            scanWifi();
            mView.setComponentsComm(true);
        }
    }


    @Override
    public void enableComm(boolean enable) {
        if (enable && mComm.getState() != CommService.STATE_CONNECTED) {
            if (!mComm.restartCommunication()) {
                startCommunication();
            }
        } else if (!enable) {
            mComm.stopComm();
        }
    }

    private void startCommunication() {
        switch (mComm.setCommunication(mHandler)) {
            case CommDirectory.E_BT_NOT_SUPPORTED:
                mView.showToast("Bluetooth not supported", false);
                mView.setComponentsComm(false);
                break;
            case CommDirectory.E_BT_OFF:
                mView.showToast("Bluetooth off, please turn on", false);
                mView.setComponentsComm(false);
                break;
        }
    }

    @Override
    public void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi) {
        WifiDirectory.getInstance().setWifi(wifi);
        new WifiDialogPresenter(dialog);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case CommService.STATE_CONNECTED:
                            Log.i(TAG, "Connected");
                            break;
                        case CommService.STATE_CONNECTING:
                            Log.i(TAG, "Connecting");
                            break;
                        case CommService.STATE_NONE:
                            Log.i(TAG, "Not connected");
                            mView.setComponentsComm(false);
                            mView.showToast("Not connected", false);
                            break;
                    }
                    break;

                case CommConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;

                case CommConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    // Concatenation of received messages
                    messageReed += readMessage;
                    // When receive the end of message, starts to process
                    if (readMessage.endsWith(((char) 4) + "")) {
                        Log.i(TAG, messageReed);
                        try {
                            JSONObject json = new JSONObject(messageReed);

                            String action = (String) json.get("ac");

                            // Possible actions from the HungryPet device
                            if (action.equals(CommDirectory.A_WIFI_GET)) {
                                sendWifiToView(json);
                            } else if (action.equals(CommDirectory.A_BT_DISCONNECT)) {
                                mView.showToast("Disconnected to wifi", false);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Json error:", e);
                        } finally {
                            messageReed = "";
                        }
                    }
                    break;

                case CommConstants.MESSAGE_DEVICE_CONNECTED:
                    //String deviceName = msg.getData().getString(CommConstants.DEVICE_NAME);
                    scanWifi();
                    mView.setComponentsComm(true);
                    break;

                case CommConstants.MESSAGE_TOAST:
                    mView.showToast(msg.getData().getString(CommConstants.TOAST), false);
                    break;

                case CommConstants.CONNECTION_FAILED:
                    mView.setComponentsComm(false);
                    break;
                case CommConstants.CONNECTION_LOST:
                    mView.setComponentsComm(false);
                    break;
            }
        }
    };

    /**
     * Send a request to scan new wifi
     */
    public boolean scanWifi() {
        boolean success = false;
        try {
            String jsScanMsg = new JSONObject()
                    .put("ac", CommDirectory.A_WIFI_GET).toString();
            success = mComm.sendMessage(jsScanMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    private void sendWifiToView(JSONObject jsWifiNetworks) {
        JSONArray arWifiNet = jsWifiNetworks.optJSONArray("cn");
        List<WifiCell> listWifiNet = new ArrayList<>();
        try {
            for (int i = 0; i < arWifiNet.length(); i++) {
                JSONObject jsWifi = arWifiNet.getJSONObject(i);
                String ssid = jsWifi.get("ssid").toString();
                WifiCell wifi = new WifiCell(ssid);
                listWifiNet.add(wifi);
            }
            mView.populateLsvWifi(listWifiNet);
        } catch (JSONException e) {
            e.printStackTrace();
            mView.showToast("Error receiving wifi", false);
        }
    }

}