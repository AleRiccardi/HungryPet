package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet.data.CommConstants;
import com.aleric.hungrypet.data.CommDirectory;
import com.aleric.hungrypet.data.CommService;
import com.aleric.hungrypet.data.WifiCell;
import com.aleric.hungrypet.data.WifiDirectory;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiDialogPresenter implements WifiContract.PresenterDialog {
    // Constants
    private static final String TAG = "wifi-main-presenter";

    private final WifiContract.ViewDialog mView;
    private final WifiDirectory mDir;
    private CommDirectory mCommDir;
    private WifiCell mWifi;

    public WifiDialogPresenter(@NonNull WifiContract.ViewDialog view) {
        mDir = WifiDirectory.getInstance();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mCommDir = CommDirectory.getInstance();
        mWifi = mDir.getWifi();

        mCommDir.setHandler(mHandler);
        mCommDir.setHandler(mHandler);

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
                            mCommDir.closeComm();
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
                            // nothing
                        } else if (action.equals(CommDirectory.A_WIFI_SET)) {
                            //@todo device connected to wifi
                            mView.showToast("Connected to wifi", false);
                            mView.dismiss();
                        } else if (action.equals(CommDirectory.A_BT_DISCONNECT)) {
                            //@todo device disconnected
                            mView.showToast("Disconnected to wifi", false);
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
                    mCommDir.closeComm();
                    break;
                case CommConstants.CONNECTION_LOST:
                    mCommDir.closeComm();
                    break;
            }
        }
    };

    @Override
    public void connectToWifi(String password) {
        mWifi.setPassword(password);
        mView.showToast("Connecting", false);
        //@todo connect to the wifi
    }
}
