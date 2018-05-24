package com.aleric.hungrypet.wifi;

import android.os.Handler;

import com.aleric.hungrypet.BasePresenter;
import com.aleric.hungrypet.BaseView;
import com.aleric.hungrypet.data.WifiCell;

import org.json.JSONException;
import org.json.JSONObject;

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