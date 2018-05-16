package com.aleric.hungrypet.wifi;

import android.support.annotation.NonNull;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.CommService;
import com.aleric.hungrypet.data.WifiCell;
import com.aleric.hungrypet.data.WifiDirectory;

public class WifiDialogPresenter implements WifiContract.PresenterDialog {

    private final WifiContract.ViewDialog mView;
    private final WifiDirectory mDir;
    private CommService mComm;
    private WifiCell mWifi;

    public WifiDialogPresenter(@NonNull WifiContract.ViewDialog view){
        mDir = WifiDirectory.getInstance();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mComm = mDir.getComm();
        mWifi = mDir.getWifi();
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

    @Override
    public void connectToWifi() {
        mView.showToast("Connecting ...");
        //@todo connect to the wifi
    }
}
