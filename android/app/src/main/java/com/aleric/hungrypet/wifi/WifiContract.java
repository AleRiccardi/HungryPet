package com.aleric.hungrypet.wifi;

import com.aleric.hungrypet.BasePresenter;
import com.aleric.hungrypet.BaseView;
import com.aleric.hungrypet.data.WifiCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface WifiContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showToast(String msg, boolean lengthLong);

        void setComponentsComm(boolean enable);

        void populateLsvWifi(List<WifiCell> listWifis);
    }

    interface ViewDialog extends BaseView<PresenterDialog> {

        boolean isActive();

        void showToast(String msg, boolean lengthLong);
    }

    interface Presenter extends BasePresenter {

        void setComm(boolean on);

        void scanWifi();

        void startDialog(WifiContract.ViewDialog dialog, WifiCell wifi);
    }

    interface PresenterDialog extends BasePresenter {

        void connectToWifi();
    }
}