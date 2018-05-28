package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet.data.Station;
import com.aleric.hungrypet.data.StationDirectory;
import com.aleric.hungrypet.data.communication.CommConstants;
import com.aleric.hungrypet.data.communication.CommDirectory;
import com.aleric.hungrypet.data.communication.CommService;
import com.aleric.hungrypet.data.wifi.WifiCell;
import com.aleric.hungrypet.data.wifi.WifiDirectory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class WifiDialogPresenter implements WifiContract.PresenterDialog {
    // Constants
    private static final String TAG = "wifi-main-mPresenter";

    private WifiContract.ViewDialog mView;
    private final WifiDirectory mDir;
    private CommDirectory mComm;
    private WifiCell mWifi;

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

                    try {
                        JSONObject jsAction = new JSONObject(readMessage);
                        String action = (String) jsAction.get("action");

                        // Possible actions from the HungryPet station
                        if (action.equals(CommDirectory.A_WIFI_SET)) {
                            JSONObject jsContent = new JSONObject(readMessage);
                            jsContent = jsContent.getJSONObject("content");
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

                    } catch (JSONException e) {
                        Log.e(TAG, "Json error:", e);
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
            String jsScanMsg = new JSONObject()
                    .put("action", CommDirectory.A_WIFI_SET)
                    .put("content", new JSONObject()
                            .put("ssid", mWifi.getSsid())
                            .put("pswd", mWifi.getPswd()))
                    .toString();
            success = mComm.sendMessage(jsScanMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

    private void connectionSuccess(String mac, String ip) {
        mView.setStatus(1);
        Station station = new Station(mac, "", ip, Calendar.getInstance().getTime());
        StationDirectory.getInstance().setStation(station); // Save the station
        CommDirectory.getInstance().closeComm(); // Close the comm.

        mView.showToast("Connected to wifi", false);
        mView.startInitActivity();
    }
}
