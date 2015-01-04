package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

public class daytimetable extends Fragment {

    int mPos;

    public static daytimetable newInstance(int position) {
        daytimetable fragment = new daytimetable();
        Bundle args = new Bundle();
        args.putInt("sec", position);
        fragment.setArguments(args);
        return fragment;
    }

    public daytimetable() {
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
        mPos = getArguments().getInt("sec");
        final View rootView = inflater.inflate(R.layout.fragment_daytimetable, container, false);

        Typeface robotoLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        Typeface robotoThin = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Thin.ttf");

        switch (mPos) {
            case 0:
                Timetable.todaysLessons = Timetable.Monday;
                break;
            case 1:
                Timetable.todaysLessons = Timetable.Tuesday;
                break;
            case 2:
                Timetable.todaysLessons = Timetable.Wednesday;
                break;
            case 3:
                Timetable.todaysLessons = Timetable.Thursday;
                break;
            case 4:
                Timetable.todaysLessons = Timetable.Friday;
                break;
        }

        LinearLayout timetableCont = (LinearLayout) rootView.findViewById(R.id.timetable_container);

        for (int i = 0; i < Timetable.times.size() - 1; i++) {
            LayoutInflater vi = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.list_item_times, null);

            TextView time = (TextView) v.findViewById(R.id.list_item_time_textview);
            LinearLayout timetextviewcont = (LinearLayout) v.findViewById(R.id.time_textview_container);
            ViewGroup.LayoutParams params = timetextviewcont.getLayoutParams();

            time.setText(Timetable.times.get(i));
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getActivity().getResources().getDisplayMetrics());

            if (Timetable.times.get(i).equals("10:20")) {
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 24, getActivity().getResources().getDisplayMetrics());
                time.setText("");
            }
            if (Timetable.times.get(i).equals("12:30")) {
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 72 / 55 * 30, getActivity().getResources().getDisplayMetrics());
                time.setText("");
            }
            if (Timetable.times.get(i).equals("13:00")) {
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 72 / 55 * 50, getActivity().getResources().getDisplayMetrics());
                time.setText(Timetable.times.get(i));
            }

            time.setTypeface(robotoThin);

            View insertPoint = rootView.findViewById(R.id.times_container);
            ((ViewGroup) insertPoint).addView(v);
        }

        for (Lesson les : Timetable.todaysLessons) {
            LayoutInflater vi = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.lesson, null);

            TextView subjectTextView = (TextView) v.findViewById(R.id.list_item_subject_textview);
            subjectTextView.setVisibility(View.VISIBLE);

            LinearLayout cont = (LinearLayout) v.findViewById(R.id.cont);

            LinearLayout subjectTextviewContainer = (LinearLayout) v.findViewById(R.id.list_item_subject_container_linearlayout);

            ViewGroup.LayoutParams contParams = cont.getLayoutParams();
            ViewGroup.LayoutParams subjectContParams = subjectTextviewContainer.getLayoutParams();

            subjectTextView.setText(les.getLessonName());
            subjectTextView.setTypeface(robotoThin);

            float units = (float) les.getLength();

            cont.setBackgroundColor(les.getBackgroundColor());

            if (units < 50) {
                subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, units / 55 * 40);
            } else {
                subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            }
            subjectTextView.setLineSpacing(-10, 1);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            textParams.setMargins(0, -10, 0, 0);
            subjectTextView.setLayoutParams(textParams);

            contParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) units / 55 * 72, v.getContext().getResources().getDisplayMetrics());
            if (les.getLength() > 55) {
                units = 55;
            }

            subjectContParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) units / 55 * 72, v.getContext().getResources().getDisplayMetrics());

            if (les.getLessonName().equals("Free Period")) {
                if (les.getLength() >= 55) {

                    String sub = "";
                    if (les.getWhoElseFree().size() == 0) {
                        sub = "No-one.";
                    }
                    if (les.getWhoElseFree().size() == 1) {
                        sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ".";
                    }
                    if (les.getWhoElseFree().size() == 2) {
                        sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + " and " + WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + ".";
                    }
                    if (les.getWhoElseFree().size() > 2) {
                        sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ", " +
                                WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + "...";
                    }

                    SpannableString txt = new SpannableString("Free Period\n With " + sub);
                    txt.setSpan(new RelativeSizeSpan(0.5f), 11, txt.length(), 0);
                    subjectTextView.setText(txt, TextView.BufferType.SPANNABLE);
                    subjectTextView.setLineSpacing(0, 1);

                    textParams.setMargins(0, 0, 0, 0);

                    subjectTextView.setLayoutParams(textParams);
                }
            }

            if (les.getLessonName().equals("Break")) {
                cont.setBackgroundColor(Color.rgb(51, 181, 229));
                contParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources().getDisplayMetrics());

                subjectContParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources().getDisplayMetrics());
                subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            if (les.getLessonName().equals("Lunch")) {
                cont.setBackgroundColor(Color.rgb(51, 181, 229));
            }

            cont.setTag(Timetable.todaysLessons.indexOf(les));
            View insertPoint = rootView.findViewById(R.id.timetable_container);
            ((ViewGroup) insertPoint).addView(v);
        }

        return rootView;
    }

}
