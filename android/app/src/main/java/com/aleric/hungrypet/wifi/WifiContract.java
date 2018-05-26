package com.aleric.hungrypet.wifi;

import com.aleric.hungrypet.BasePresenter;
import com.aleric.hungrypet.BaseView;
import com.aleric.hungrypet.data.wifi.WifiCell;

import java.util.List;

public interface WifiContract {


    interface View extends BaseView<Presenter> {

        boolean isActive();

        void setComponentsComm(boolean enable);

        void populateLsvWifi(List<WifiCell> listWifiNet);
    }

    interface ViewDialog extends BaseView<PresenterDialog> {

        void dismiss();

        boolean isActive();

        /**
         * Set the message of the TextView Status
         *
         * @param state with 0 -> Connecting, 1 -> Connected, 2 -> not Connected
         */
        void setStatus(int state);

        void startInitActivity();

    }


    interface Presenter extends BasePresenter {

        void enableComm(boolean enable);

        boolean scanWifi();

        void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi);
    }

    interface PresenterDialog extends BasePresenter {

        boolean connectToWifi(String password);

    }
}