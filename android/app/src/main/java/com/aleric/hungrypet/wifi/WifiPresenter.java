package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet.data.CommDirectory;
import com.aleric.hungrypet.data.CommService;
import com.aleric.hungrypet.data.CommConstants;
import com.aleric.hungrypet.data.WifiCell;
import com.aleric.hungrypet.data.WifiDirectory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WifiPresenter implements WifiContract.Presenter {

    private static final String TAG = "wifi-presenter";

    private final WifiContract.View mView;
    /**
     * Member object for the chat services
     */
    private CommService mComm = null;
    /**
     * Communication service instance
     */
    private CommDirectory mCommDir;
    /**
     * Set if it's the first time that we start the activity
     */
    private boolean mIsNewActivity;


    public WifiPresenter(@NonNull WifiContract.View wifiView, boolean isNewActivity) {
        mView = wifiView;
        mView.setPresenter(this);
        mIsNewActivity = isNewActivity;
        mCommDir = CommDirectory.getInstance();
    }

    @Override
    public void start() {
        if (mIsNewActivity) {
            startCommunication();
        }
    }

    @Override
    public void enableComm(boolean enable) {
        if (enable && mCommDir.getState() != CommService.STATE_CONNECTED) {
            if (!mCommDir.restartComm()) {
                startCommunication();
            }
        } else if (mCommDir.getState() != CommService.STATE_NONE){
            mCommDir.closeComm();
        }
    }

    private void startCommunication(){
        switch (mCommDir.setComm(mHandler)) {
            case CommDirectory.E_BT_NOT_SUPPORTED:
                mView.showToast("Bluetooth not supported", false);
                mView.setComponentsComm(false);
                break;
            case CommDirectory.E_BT_OFF:
                mView.showToast("Bluetooth off, please turn on", false);
                mView.setComponentsComm(false);
                break;
            case CommDirectory.E_BT_ERROR:
                mView.showToast("Bluetooth error, restart the application", false);
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
                            //mWifiArrayAdapter.clear();
                            break;
                        case CommService.STATE_CONNECTING:
                            Log.i(TAG, "Connecting");
                            break;
                        case CommService.STATE_NONE:
                            Log.i(TAG, "Not connected");
                            if (mCommDir.closeComm()) mView.setComponentsComm(false);
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

                    try {
                        JSONObject json = new JSONObject(readMessage);
                        String action = (String) json.get("action");

                        // Possible actions from the HungryPet device
                        if (action.equals(CommDirectory.A_WIFI_GET)) {
                            sendWifisToView(json);
                        } else if (action.equals(CommDirectory.A_WIFI_SET)) {
                            //@todo device connected to wifi
                            mView.showToast("Connected to wifi", false);
                        } else if (action.equals(CommDirectory.A_BT_DISCONNECT)) {
                            //@todo device disconnected
                            mView.showToast("Disconnected to wifi", false);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Json error:", e);
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
                    if (mCommDir.closeComm()) mView.setComponentsComm(false);
                    break;
                case CommConstants.CONNECTION_LOST:
                    if (mCommDir.closeComm()) mView.setComponentsComm(false);
                    break;
            }
        }
    };


    /**
     * Scan the wifi and display in the ListView of the view.
     */
    @Override
    public void scanWifi() {
        boolean success = false;
        try {
            String jsScanMsg = new JSONObject()
                    .put("action", CommDirectory.A_WIFI_GET).toString();
            success = mCommDir.sendMessage(jsScanMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (success) mView.showToast("Wifi networks scanned", false);
        else mView.showToast("Couldn't scan the wifi", false);
    }

    private void sendWifisToView(JSONObject jsWifis) {
        JSONArray arWifis = jsWifis.optJSONArray("content");
        List<WifiCell> listWifis = new ArrayList<>();
        try {
            for (int i = 0; i < arWifis.length(); i++) {
                JSONObject jsWifi = arWifis.getJSONObject(i);
                String ssid = jsWifi.get("ssid").toString();
                String encryption = jsWifi.get("encryption").toString();

                WifiCell wifi = new WifiCell(ssid, encryption);
                listWifis.add(wifi);
            }
            mView.populateLsvWifi(listWifis);
        } catch (JSONException e) {
            e.printStackTrace();
            mView.showToast("Error receiving wifi", false);
        }
    }

}
