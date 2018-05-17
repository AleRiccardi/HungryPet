package com.aleric.hungrypet.wifi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.WifiCell;

import java.util.List;

public class WifiFragment extends Fragment implements WifiContract.View {

    // CommConstants
    private static final String TAG = "WifiFragmentOld";

    // Variables
    private WifiContract.Presenter mPresenter;

    private TextView txvPlaceholder;

    private ListView lsvWifi;

    private FloatingActionButton fabScan;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<WifiCell> mWifiAdapter;


    public WifiFragment() {
    }

    public static WifiFragment newInstance() {
        return new WifiFragment();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(WifiContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        txvPlaceholder = view.findViewById(R.id.txv_placeholder);
        lsvWifi = view.findViewById(R.id.lsv_wifi);
        fabScan = view.findViewById(R.id.fab_refresh);

        if (getActivity() != null) {
            // Initialize the array adapter for the conversation thread
            mWifiAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_wifi);
            lsvWifi.setAdapter(mWifiAdapter);

            lsvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position >= 0) {
                        WifiCell wifi = (WifiCell) adapterView.getAdapter().getItem(position);
                        WifiDialogFragment dialog = WifiDialogFragment.newInstance();

                        mPresenter.startDialog(dialog, wifi);
                        dialog.show(getFragmentManager().beginTransaction(), "dialog");
                    }
                }
            });

            fabScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.scanWifi();
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.closeComm();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void blockComponents() {
        fabScan.setEnabled(false);
        fabScan.setClickable(false);
    }

    @Override
    public void enableComponents() {
        fabScan.setEnabled(true);
        fabScan.setClickable(true);
    }

    public void populateLsvWifi(List<WifiCell> listWifis) {
        mWifiAdapter.clear();
        mWifiAdapter.addAll(listWifis);
        fabScan.setEnabled(true);
    }
}
