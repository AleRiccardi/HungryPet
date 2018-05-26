package com.aleric.hungrypet.initDevice;

import com.aleric.hungrypet.BasePresenter;
import com.aleric.hungrypet.BaseView;
import com.aleric.hungrypet.data.wifi.WifiCell;

import java.util.List;

public interface InitDeviceContract {


    interface View extends BaseView<Presenter> {

        boolean isActive();

    }


    interface Presenter extends BasePresenter {


    }
}