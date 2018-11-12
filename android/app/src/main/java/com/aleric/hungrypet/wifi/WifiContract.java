package com.aleric.hungrypet.wifi;

import com.aleric.hungrypet.BaseContract;
import com.aleric.hungrypet._data.wifi.WifiCell;

import java.util.List;

public interface WifiContract {


    interface View extends BaseContract.View<Presenter>{

        boolean isActive();

        void setComponentsComm(boolean enable);

        void populateLsvWifi(List<WifiCell> listWifiNet);
    }

    interface ViewDialog extends BaseContract.View<PresenterDialog> {

        boolean isActive();

        void dismiss();

        /**
         * Set the message of the TextView Status
         *
         * @param state with 0 -> Connecting, 1 -> Connected, 2 -> not Connected
         */
        void setStatus(int state);

        void startInitActivity();

    }


    interface Presenter extends BaseContract.Presenter {

        void enableComm(boolean enable);

        boolean scanWifi();

        void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi);
    }

    interface PresenterDialog extends  BaseContract.Presenter {

        boolean connectToWifi(String password);

    }
}