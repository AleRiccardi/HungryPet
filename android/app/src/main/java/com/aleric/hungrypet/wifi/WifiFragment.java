package com.aleric.hungrypet.wifi;

import android.content.res.ColorStateList;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.WifiCell;

import java.util.ArrayList;
import java.util.List;

public class WifiFragment extends Fragment implements WifiContract.View {

    // CommConstants
    private static final String TAG = "WifiFragmentOld";

    // Variables
    private WifiContract.Presenter mPresenter;

    private TextView txvState;

    private Switch swcStateAction;

    private TextView txvPlaceholder;

    private ListView lsvWifi;

    private FloatingActionButton fabRefresh;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        txvState = view.findViewById(R.id.txv_state);
        swcStateAction = view.findViewById(R.id.swc_state_action);
        txvPlaceholder = view.findViewById(R.id.txv_placeholder);
        lsvWifi = view.findViewById(R.id.lsv_wifi);
        fabRefresh = getActivity().findViewById(R.id.fab_refresh);

        if (getActivity() != null) {
            // Initialize the array adapter for the conversation thread
            //mWifiAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_wifi);
            //lsvWifi.setAdapter(mWifiAdapter);

            // Construct the data source
            ArrayList<WifiCell> arrayOfWifi = new ArrayList<>();
            // Create the adapter to convert the array to views
            mWifiAdapter = new WifiAdapter(getActivity(), arrayOfWifi);
            // Attach the adapter to a ListView
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

            swcStateAction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mPresenter.setComm(isChecked);
                }
            });

            fabRefresh.setOnClickListener(new View.OnClickListener() {
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
        mPresenter.setComm(false);
    }

    @Override
    public void showToast(String msg) {
        boolean longDuration = true;
        Toast.makeText(getActivity(), msg, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }


    @Override
    public void enableComm() {
        // TextView --> On
        txvState.setText(R.string.state_on);
        // Switch --> Checked
        swcStateAction.setChecked(true);
        // ListView --> Clear
        mWifiAdapter.clear();
        // FloatingActionButton --> Enable
        fabRefresh.setEnabled(true);
        fabRefresh.setClickable(true);
        fabRefresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
    }

    @Override
    public void disableComm() {
        // TextView --> Off
        txvState.setText(R.string.state_off);
        // Switch --> Unchecked
        swcStateAction.setChecked(false);
        // ListView --> Clear
        mWifiAdapter.clear();
        // FloatingActionButton --> Enable
        fabRefresh.setEnabled(false);
        fabRefresh.setClickable(false);
        fabRefresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.inactiveGray)));
    }

    public void populateLsvWifi(List<WifiCell> listWifis) {
        mWifiAdapter.clear();
        mWifiAdapter.addAll(listWifis);
        fabRefresh.setEnabled(true);
    }


}
