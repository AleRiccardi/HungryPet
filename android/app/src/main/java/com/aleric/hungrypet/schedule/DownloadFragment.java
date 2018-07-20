package com.aleric.hungrypet.schedule;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.shedule.SynchronizeSchedule;

/**
 * Per dettagli vedere: server-connection-example, exercise-2 (laboratorio 1).
 */
public class DownloadFragment extends Fragment {

    private DownloadListener mListener;
    private Activity mActivity = null;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        mActivity = getActivity();
        new SynchronizeSchedule(getActivity(), mListener).execute();
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DownloadListener) {
            mListener = (DownloadListener) context;
        } else {
            Log.e("DownloadFragment", "not possible to initialize the listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
