package com.aleric.hungrypet.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.wifi.WifiDirectory;
import com.aleric.hungrypet.init.InitActivity;

public class WifiDialogFragment extends DialogFragment implements WifiContract.ViewDialog {
    private static final String TAG = "WifiDialogFragment";
    private static final String DIALOG_PRESENTER = "mPresenter";
    private static final String DIALOG_WIFI = "wificell";

    private WifiContract.PresenterDialog mPresenter;

    // Layout Views
    private EditText edtPassword;
    private TextView txvStatus;

    public WifiDialogFragment() {
    }

    public static WifiDialogFragment newInstance() {
        return new WifiDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wifi, container, false);
        WifiDirectory dir = WifiDirectory.getInstance();
        String ssid = dir.getWifi().getSsid();
        Button btnSingIn = view.findViewById(R.id.btn_sing_in);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        TextView txvSsid = view.findViewById(R.id.txv_ssid);
        edtPassword = view.findViewById(R.id.edt_pswd);
        txvStatus = view.findViewById(R.id.txv_status);
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
        txvSsid.setText(ssid);

        return view;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    @Override
    public void setStatus(int state) {
        String msg = state == 0 ? "Connecting ..." : state == 1 ? "Connected" : "Not connected";
        txvStatus.setText(msg);
    }

    @Override
    public void startInitActivity() {
        Intent intent = new Intent(getActivity(), InitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void setPresenter(WifiContract.PresenterDialog presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onResume() {
        super.onResume();
        attachPresenter();
        mPresenter.start();
    }

    private void attachPresenter() {
        if(mPresenter == null) {
            mPresenter = new WifiDialogPresenter(this);
        }
    }


    @Override
    public void showToast(String msg, boolean lengthLong) {
        Toast.makeText(getActivity(), msg, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}