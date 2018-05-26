package com.aleric.hungrypet;

public interface BaseView<T> {

    void setPresenter(T presenter);

    /**
     * @// TODO: 26/05/2018 Create a class with utility action like this
     * @param msg
     * @param lengthLong
     */
    void showToast(String msg, boolean lengthLong);

}