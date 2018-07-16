package com.aleric.hungrypet.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.wifi.WifiCell;

import java.util.ArrayList;

public class WifiAdapter extends ArrayAdapter<WifiCell> {
    // View lookup cache
    private static class ViewHolder {
        TextView txvSsid;
        TextView txvEncryption;
    }


    public WifiAdapter(Context context, ArrayList<WifiCell> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for position
        WifiCell wifi = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_wifi, parent, false);
        }
        // Lookup view for data population
        TextView txvSsid = (TextView) convertView.findViewById(R.id.txv_ssid);
        TextView txvEncryption = (TextView) convertView.findViewById(R.id.txv_encryption);
        // Populate the data into the template view using the data object
        txvSsid.setText(wifi.getSsid());
        txvEncryption.setText("Encryption: " + wifi.getEncryption());
        // Return the completed view to render on screen
        return convertView;
    }
}