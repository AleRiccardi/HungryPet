package com.aleric.hungrypet.wifi;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.WifiDirectory;

public class WifiDialogFragment extends DialogFragment implements WifiContract.ViewDialog{
    private static final String TAG = "WifiDialogFragment";
    private static final String DIALOG_PRESENTER = "presenter";
    private static final String DIALOG_WIFI = "wificell";

    private WifiContract.PresenterDialog mPresenter;

    // Layout Views
    private TextView txvSsid;
    private EditText edtPassword;

    private Button btnSingIn;
    private Button btnCancel;

    public WifiDialogFragment() {
    }

    public static WifiDialogFragment newInstance() {
        return new WifiDialogFragment();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(WifiContract.PresenterDialog presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wifi, container, false);
        WifiDirectory dir = WifiDirectory.getInstance();
        String ssid = dir.getWifi().getSsid();

        txvSsid = view.findViewById(R.id.txv_ssid);
        edtPassword = view.findViewById(R.id.edt_pswd);
        btnSingIn = view.findViewById(R.id.btn_sing_in);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnSingIn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.connectToWifi(edtPassword.getText().toString());
            }
        }));

        btnCancel.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        }));


        txvSsid .setText(ssid);

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showToast(String msg, boolean lengthLong){
        Toast.makeText(getActivity(), msg, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}