package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aleric.hungrypet.R;
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
    /**
     * Communication action, same in raspberry program.
     */
    public static final String A_WIFI_GET = "wifi-get";
    public static final String A_WIFI_CONNECTION = "wifi-connection";
    public static final String A_BT_DISCONNECT = "bt-quit";

    private final WifiContract.View mView;
    /**
     * Name of the connected device
     */
    private String mDeviceName = null;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Member object for the chat services
     */
    private CommService mComm = null;


    public WifiPresenter(@NonNull WifiContract.View wifiView) {
        mView = wifiView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            mView.showToast("Bluetooth not supported");
            mView.blockComponents();
        } else {
            setUpComm();
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
                            closeComm();
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
                        if (action.equals(WifiPresenter.A_WIFI_GET)) {
                            sendWifisToView(json);
                        } else if (action.equals(WifiPresenter.A_WIFI_CONNECTION)) {
                            //@todo device connected to wifi
                            mView.showToast("Connected to wifi");
                        } else if (action.equals(WifiPresenter.A_BT_DISCONNECT)) {
                            //@todo device disconnected
                            mView.showToast("Disconnected to wifi");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Json error:", e);
                    }
                    break;

                case CommConstants.MESSAGE_DEVICE_NAME:
                    mDeviceName = msg.getData().getString(CommConstants.DEVICE_NAME);
                    mView.showToast("Connected with " + mDeviceName + " device");
                    scanWifi();
                    mView.enableComponents();
                    break;

                case CommConstants.MESSAGE_TOAST:
                    mView.showToast(msg.getData().getString(CommConstants.TOAST));
                    break;

                case CommConstants.CONNECTION_FAILED:
                    closeComm();
                    break;
                case CommConstants.CONNECTION_LOST:
                    closeComm();
                    break;
            }
        }
    };

    @Override
    public void setUpComm() {
        if ((mBluetoothAdapter != null) && (!mBluetoothAdapter.isEnabled())) {
            mView.showToast("Bluetooth off, please turn on");
        } else if (mBluetoothAdapter != null) {
            if (mComm == null) {
                mComm = new CommService(mHandler);
            }
        }

    }

    /**
     * Performing this check in onResume() covers the case in which BT was
     * not enabled during onStart(), so we were paused to enable it...
     * onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
     */
    @Override
    public void resumeComm() {
        if (mComm != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mComm.getState() == CommService.STATE_NONE) {
                // Start the Bluetooth chat services
                mComm.start();
            }
        }
    }

    /**
     * Close the communication.
     */
    @Override
    public void closeComm() {
        if (mComm != null) {
            mComm.stop();
            mComm = null;
            mView.blockComponents();
        }
    }

    /**
     * Sends a message.
     *
     * @param msg A string of text to send.
     */
    private boolean sendMessage(String msg) {
        // Check that we're actually connected before trying anything
        if (mComm.getState() != CommService.STATE_CONNECTED) {
            mView.showToast(R.string.not_connected + "");
            return false;
        }

        // Check that there's actually something to send
        if (msg.length() > 0) {
            // Get the message bytes and tell the CommService to write
            byte[] send = msg.getBytes();
            mComm.write(send);
            return true;
        }
        return false;
    }

    /**
     * Scan the wifi and display in the ListView of the view.
     */
    @Override
    public void scanWifi() {
        boolean success = false;
        try {
            String jsScanMsg = new JSONObject()
                    .put("action", WifiPresenter.A_WIFI_GET).toString();
            success = sendMessage(jsScanMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!success) mView.showToast("Couldn't scan the wifi");
    }

    private void sendWifisToView(JSONObject jsWifis) {
        JSONArray arWifis = jsWifis.optJSONArray("content");
        List<WifiCell> listWifis = new ArrayList<>();
        try {
            for (int i = 0; i < arWifis.length(); i++) {
                JSONObject jsWifi = arWifis.getJSONObject(i);
                String ssid = jsWifi.get("ssid").toString();
                WifiCell wifi = new WifiCell(ssid);
                listWifis.add(wifi);
            }
            mView.populateLsvWifi(listWifis);
        } catch (JSONException e) {
            e.printStackTrace();
            mView.showToast("Error receiving wifi");
        }
    }

    @Override
    public void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi) {
        WifiDirectory.getInstance().setWifi(wifi);
        new WifiDialogPresenter(dialog);
    }

}
