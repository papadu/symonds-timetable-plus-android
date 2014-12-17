package com.psyngo.michael.symondstimetableplus;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LessonDetailFragment extends Fragment {

    LinearLayout mainll;

    public LessonDetailFragment(){}

    public static LessonDetailFragment newInstance(int sectionNumber) {
        LessonDetailFragment fragment = new LessonDetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Lesson x = Timetable.clickedLesson;
        mainll = (LinearLayout) inflater.inflate(R.layout.fragment_detail_lesson, container, false);

        TextView time = (TextView) mainll.findViewById(R.id.detail_timetextview);
        TextView subject = (TextView) mainll.findViewById(R.id.detail_subjecttextview);
        TextView room = (TextView) mainll.findViewById(R.id.detail_roomtextview);
        TextView teacher = (TextView) mainll.findViewById(R.id.detail_teacherteactview);

        time.setText(x.getLessonTime());
        subject.setText(x.getLessonName());
        room.setText(x.getLessonRoom());
        teacher.setText(x.getLessonTeacher());
        return mainll;
    }
}
