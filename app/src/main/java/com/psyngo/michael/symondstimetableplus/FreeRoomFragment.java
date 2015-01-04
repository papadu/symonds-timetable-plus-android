package com.psyngo.michael.symondstimetableplus;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FreeRoomFragment extends Fragment {

    public static FreeRoomFragment newInstance() {
        FreeRoomFragment fragment = new FreeRoomFragment();
        return fragment;
    }

    public FreeRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_free_room, container, false);
    }



}
