package com.aleric.hungrypet;

import android.arch.lifecycle.Lifecycle;

import com.aleric.hungrypet.wifi.WifiContract;

public interface BaseContract {

    interface View<T> {

        void setPresenter(T presenter);

        /**
         * @// TODO: 26/05/2018 Create a class with utility action like this
         * @param msg
         * @param lengthLong
         */
        void showToast(String msg, boolean lengthLong);
    }

    interface Presenter {

        void start();
    }
}