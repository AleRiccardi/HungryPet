package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;
import com.aleric.hungrypet._data.communication.CommConstants;
import com.aleric.hungrypet._data.communication.CommDirectory;
import com.aleric.hungrypet._data.communication.CommService;
import com.aleric.hungrypet._data.wifi.WifiCell;
import com.aleric.hungrypet._data.wifi.WifiDirectory;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiDialogPresenter implements WifiContract.PresenterDialog {
    // Constants
    private static final String TAG = "wifi-main-mPresenter";

    private WifiContract.ViewDialog mView;
    private final WifiDirectory mDir;
    private CommDirectory mComm;
    private WifiCell mWifi;
    static String messageReed = "";
    private String mJsScanMsg = "";
    private int mTentativeError = 0;


    public WifiDialogPresenter(@NonNull WifiContract.ViewDialog view) {
        mDir = WifiDirectory.getInstance();
        mView = view;
        mView.setPresenter(this);
        mComm = CommDirectory.getInstance();
    }

    @Override
    public void start() {
        if (mComm.getState() == CommService.STATE_NONE) {
            mView.dismiss();
        } else if (mComm.getState() == CommService.STATE_CONNECTED) {
            mComm.setHandler(mHandler);
            mWifi = mDir.getWifi();

            mComm.setHandler(mHandler);
        }
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
                        // TODO error for null messages like -> (null{"action":"wifi-set","content":{"ip": ...)
                        Log.i(TAG, messageReed);
                        try {
                            JSONObject jsAction = new JSONObject(messageReed);
                            String action = (String) jsAction.get("ac");

                            // Possible actions from the HungryPet station
                            if (action.equals(CommDirectory.A_WIFI_SET)) {
                                JSONObject jsContent = new JSONObject(messageReed);
                                jsContent = jsContent.getJSONObject("cn");
                                String status = (String) jsContent.get("status");

                                if (status.equals("success")) {
                                    String mac = (String) jsContent.get("mac");
                                    String ip = (String) jsContent.get("ip");
                                    connectionSuccess(mac, ip);
                                } else {
                                    mView.setStatus(2);
                                    mView.showToast("Not connected to wifi", false);
                                }
                            } else if (action.equals(CommDirectory.A_BT_DISCONNECT)) {
                                mView.showToast("Disconnected to wifi", false);
                                mView.dismiss();
                            }
                            mTentativeError = 0;
                        } catch (JSONException e) {
                            Log.e(TAG, "Json error:", e);
                            if(mTentativeError < 4){
                                mComm.sendMessage(mJsScanMsg);
                                mTentativeError++;
                            }
                        } finally {
                            messageReed = "";
                        }
                    }
                    break;

                case CommConstants.MESSAGE_DEVICE_CONNECTED:
                    String mDeviceName = msg.getData().getString(CommConstants.DEVICE_NAME);
                    break;

                case CommConstants.MESSAGE_TOAST:
                    mView.showToast(msg.getData().getString(CommConstants.TOAST), false);
                    break;

                case CommConstants.CONNECTION_FAILED:
                    break;
                case CommConstants.CONNECTION_LOST:
                    break;
            }
        }
    };

    @Override
    public boolean connectToWifi(String password) {
        mView.setStatus(0);
        mWifi.setPassword(password);
        boolean success = false;
        try {
            mJsScanMsg = new JSONObject()
                    .put("ac", CommDirectory.A_WIFI_SET)
                    .put("cn", new JSONObject()
                            .put("ssid", mWifi.getSsid())
                            .put("pswd", mWifi.getPswd()))
                    .toString();
            success = mComm.sendMessage(mJsScanMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

    private void connectionSuccess(String mac, String ip) {
        mView.setStatus(1);
        Station station = new Station(mac, "", ip);
        StationDirectory.getInstance().setStation(station); // Save the station
        CommDirectory.getInstance().closeComm(); // Close the comm.

        mView.showToast("Connected to wifi", false);
        mView.startInitActivity();
    }
}
