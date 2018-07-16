package com.aleric.hungrypet.wifi;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.aleric.hungrypet._data.wifi.WifiCell;

import java.util.ArrayList;
import java.util.List;

public class WifiFragment extends Fragment implements WifiContract.View {

    // CommConstants
    private static final String TAG = "WifiFragmentOld";
    private static final String NO_TRIGGER = "no_trigger";

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
        fabRefresh = getActivity().findViewById(R.id.fab_add_schedule);

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
                        WifiCell wifiCell = (WifiCell) adapterView.getAdapter().getItem(position);
                        WifiDialogFragment dialogFragment = WifiDialogFragment.newInstance();
                        mPresenter.startDialog(dialogFragment, wifiCell);
                        dialogFragment.show(getFragmentManager().beginTransaction(), "dialog");
                    }
                }
            });

            swcStateAction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mPresenter.enableComm(isChecked);
                }
            });

            fabRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPresenter.scanWifi()){
                        Snackbar.make(v, "Scanned new wifi from the HungryPet station", Snackbar.LENGTH_LONG).show();
                    } else {
                        showToast("Couldn't scan the the wifi networks", false);
                    }
                }
            });

            setComponentsComm(false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        attachPresenter();
        mPresenter.start();
    }

    private void attachPresenter() {
        if(mPresenter == null) {
            mPresenter = new WifiPresenter(this);
        }
    }



    @Override
    public void showToast(String msg, boolean lengthLong) {
        if(getActivity() != null) {
            Toast.makeText(getActivity(), msg, lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setComponentsComm(boolean enable) {
        if (isActive()) {
            int stateString;
            ColorStateList colorFab;
            if (enable) {
                stateString = R.string.state_on;
                colorFab = ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary));
            } else {
                stateString = R.string.state_off;
                colorFab = ColorStateList.valueOf(getResources().getColor(R.color.inactiveGray));
            }
            // TextView
            txvState.setText(stateString);
            // Switch
            swcStateAction.setChecked(enable);
            // ListView
            mWifiAdapter.clear();
            // FloatingActionButton
            fabRefresh.setEnabled(enable);
            fabRefresh.setClickable(enable);
            fabRefresh.setBackgroundTintList(colorFab);
        }

    }

    public void populateLsvWifi(List<WifiCell> listWifiNet) {
        mWifiAdapter.clear();
        mWifiAdapter.addAll(listWifiNet);
        fabRefresh.setEnabled(true);
    }


}