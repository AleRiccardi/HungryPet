package com.aleric.hungrypet.wifi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleric.hungrypet.data.CommConstants;
import com.aleric.hungrypet.data.CommService;
import com.aleric.hungrypet.data.WifiCell;
import com.aleric.hungrypet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @todo Make appear a connect button when there's no connection to bluetooth or connection lost
 * @todo Better graphic for wifi_dialog list
 * @todo Enter passw for wifi_dialog
 */
public class WifiFragmentOld extends Fragment  {

    // CommConstants
    private static final String TAG = "WifiFragmentOld";
    public static final String A_WIFI_GET = "wifi-get";
    public static final String A_WIFI_CONNECTION = "wifi-connection";
    public static final String A_BT_DISCONNECT = "bt-quit";

    // Variables
    private TextView txvPlaceholder;
    private ListView lsvWifi;
    private Button btnScan;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<WifiCell> mWifiArrayAdapter;
    /**
     * Member object for the chat services
     */
    private CommService mChatService = null;

    public interface OnClickInteraction {
    }

    private OnClickInteraction listener;

    public WifiFragmentOld() {
    }

    public static WifiFragmentOld newInstance() {
        WifiFragmentOld instance = new WifiFragmentOld();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        txvPlaceholder = view.findViewById(R.id.txv_placeholder);
        lsvWifi = view.findViewById(R.id.lsv_wifi);
        btnScan = view.findViewById(R.id.fab_refresh);

        lsvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*WifiCell wifi = (WifiCell) adapterView.getAdapter().getItem(position);
                if (listener != null) {
                    DialogFragment dialogFrag = WifiDialogFragment.newInstance(123);
                    dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
                }*/
                DialogFragment dialogFrag = WifiDialogFragment.newInstance();
                dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("{ \"action\": \"" + WifiFragmentOld.A_WIFI_GET +"\" }");
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getActivity(), "Bluetooth off, please turn on",
                    Toast.LENGTH_SHORT).show();
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == CommService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WifiFragmentOld.OnClickInteraction) {
            listener = (WifiFragmentOld.OnClickInteraction) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        listener = null;

    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        /**
         * @todo on connection lost
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case CommConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case CommService.STATE_CONNECTED:
                            Log.i(TAG, "Connected");
                            mWifiArrayAdapter.clear();
                            break;
                        case CommService.STATE_CONNECTING:
                            Log.i(TAG, "Connecting");
                            break;
                        case CommService.STATE_NONE:
                            Log.i(TAG, "Not connected");
                            disconnected();
                            break;
                    }
                    break;
                case CommConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(activity, writeMessage, Toast.LENGTH_SHORT).show();
                    break;
                case CommConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    try {
                        JSONObject json = new JSONObject(readMessage);
                        String action = (String) json.get("action");
                        if (action.equals(WifiFragmentOld.A_WIFI_GET)) {
                            populateLsvWifi(json);
                        } else if (action.equals(WifiFragmentOld.A_WIFI_CONNECTION)) {

                        } else if (action.equals(WifiFragmentOld.A_BT_DISCONNECT)) {

                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Json error:", e);
                        Toast.makeText(activity, mConnectedDeviceName + ": " + readMessage, Toast.LENGTH_LONG).show();
                    }
                    break;
                case CommConstants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(CommConstants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CommConstants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(CommConstants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mWifiArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_wifi);
        lsvWifi.setAdapter(mWifiArrayAdapter);

        // Initialize the CommService to perform bluetooth connections
        mChatService = new CommService(mHandler);
        btnScan.setEnabled(true);
        btnScan.setClickable(true);
    }

    private void disconnected(){
        btnScan.setEnabled(false);
        btnScan.setClickable(false);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != CommService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the CommService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    private void populateLsvWifi(JSONObject jsWifis) throws JSONException {
        btnScan.setEnabled(false);

        JSONArray arWifis =  jsWifis.optJSONArray("content");
        for (int i = 0; i < arWifis.length(); i++){
            JSONObject jsWifi = arWifis.getJSONObject(i);
            String ssid = jsWifi.get("ssid").toString();
            String encryption = jsWifi.get("encryption").toString();
            WifiCell wifiCell = new WifiCell(ssid);
            mWifiArrayAdapter.add(wifiCell);
        }

        btnScan.setEnabled(true);
    }


}
