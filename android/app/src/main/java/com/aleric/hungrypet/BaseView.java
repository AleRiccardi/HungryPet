package com.aleric.hungrypet;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showToast(String msg, boolean lengthLong);

}