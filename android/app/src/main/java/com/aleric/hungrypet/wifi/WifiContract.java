package com.aleric.hungrypet.wifi;

import com.aleric.hungrypet.BasePresenter;
import com.aleric.hungrypet.BaseView;
import com.aleric.hungrypet.data.WifiCell;

import org.json.JSONException;
import org.json.JSONObject;

public interface WifiContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showToast(String msg);

        void blockComponents();

        void enableComponents();

        void populateLsvWifi(JSONObject jsWifis) throws JSONException;
    }

    interface ViewDialog extends BaseView<PresenterDialog> {

        boolean isActive();

        void showToast(String msg);
    }

    interface Presenter extends BasePresenter {

        void setUpComm();

        void resumeComm();

        void closeComm();

        void scanWifi();

        void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi);
    }

    interface PresenterDialog extends BasePresenter {

        void connectToWifi();
    }
}