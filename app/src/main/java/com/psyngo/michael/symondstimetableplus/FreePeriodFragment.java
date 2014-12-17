package com.psyngo.michael.symondstimetableplus;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;


public class FreePeriodFragment extends Fragment {
    int sectionNum;

    public FreePeriodFragment(){}

    public FreePeriodFragment(int sectionNum){
        this.sectionNum = sectionNum;
    }


    public static FreePeriodFragment newInstance(int sectionNumber) {
        FreePeriodFragment fragment = new FreePeriodFragment(sectionNumber);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Lesson x = Timetable.clickedLesson;
        LinearLayout mainll = (LinearLayout) inflater.inflate(R.layout.fragment_detail_free, container, false);
        LinearLayout textll = (LinearLayout) mainll.findViewById(R.id.freeTextLL);
        TextView tv = (TextView) mainll.findViewById(R.id.timeTV);
        TextView with = (TextView) mainll.findViewById(R.id.with);
        ListView lv = (ListView) mainll.findViewById(R.id.whoElseFreeListview);

        if(sectionNum==0){
            textll.setVisibility(View.GONE);
            with.setVisibility(View.GONE);
        }
        else{
            tv.setText("at " + x.getLessonTime());
            textll.setVisibility(View.VISIBLE);
            with.setVisibility(View.VISIBLE);
        }


        List<String> data = new ArrayList<String>();
        for (String s: x.getWhoElseFree()){
            data.add(WordUtils.capitalizeFully(s));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);
        return mainll;
    }




}
