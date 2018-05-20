package com.aleric.hungrypet.wifi;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.aleric.hungrypet.R;

public class WifiDialogFragment extends DialogFragment implements WifiContract.ViewDialog{
    private static final String TAG = "WifiDialogFragment";
    private static final String DIALOG_PRESENTER = "presenter";
    private static final String DIALOG_WIFI = "wificell";

    private WifiContract.PresenterDialog mPresenter;

    // Layout Views
    private EditText mSSIDEditText;
    private EditText mPWDEditText;

    private boolean isValidInput;

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


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_wifi, null))
                // Add action buttons
                .setPositiveButton("Sing in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mPresenter.connectToWifi();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WifiDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();


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