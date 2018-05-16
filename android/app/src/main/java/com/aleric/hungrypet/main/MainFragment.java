package com.aleric.hungrypet.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aleric.hungrypet.R;


public class MainFragment extends Fragment {

    private Button btnConfigure;

    public interface OnClickInteraction {
        void onClickConfigure();
    }

    private OnClickInteraction listener;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment instance = new MainFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        btnConfigure = view.findViewById(R.id.btn_configure);

        btnConfigure.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClickConfigure();
                }
            }
        }));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickInteraction) {
            listener = (OnClickInteraction) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }
}
