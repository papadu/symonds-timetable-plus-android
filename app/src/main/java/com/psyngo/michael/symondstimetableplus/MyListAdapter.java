package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michael on 19/10/2014.
 */
public class MyListAdapter extends ArrayAdapter<Lesson> {


    List<Lesson> objects;
    Context context;
    LayoutInflater mInflater;

    public MyListAdapter(Context context, int resource, List<Lesson> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {


            itemView = mInflater.inflate(R.layout.lesson, parent, false);


        }


        Lesson currentLesson = objects.get(position);


        TextView subjectTextView = (TextView) itemView.findViewById(R.id.list_item_subject_textview);


        String fontpathlight = "fonts/Roboto-Light.ttf";
        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), fontpathlight);

        Typeface robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        subjectTextView.setTypeface(robotoThin);


        subjectTextView.setVisibility(View.VISIBLE);


        LinearLayout container = (LinearLayout) itemView.findViewById(R.id.list_item_container_linearlayout);

        LinearLayout subjectTextviewContainer = (LinearLayout) itemView.findViewById(R.id.list_item_subject_container_linearlayout);

        ViewGroup.LayoutParams contParams = container.getLayoutParams();
        ViewGroup.LayoutParams subjectContParams = subjectTextviewContainer.getLayoutParams();


        subjectTextView.setText(currentLesson.getLessonName());


        container.setBackgroundColor(currentLesson.getBackgroundColor());

        subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        subjectTextView.setLineSpacing(-10, 1);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        textParams.setMargins(5, -10, 0, 0);
        subjectTextView.setLayoutParams(textParams);



        contParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, context.getResources().getDisplayMetrics());
        subjectContParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, context.getResources().getDisplayMetrics());


        if (currentLesson.getLessonName().equals("Free Period")){
            SpannableString txt = new SpannableString("Free Period\n With Elliot, Mckenzie...");
            txt.setSpan(new RelativeSizeSpan(0.5f), 11, txt.length(),0);
            subjectTextView.setText(txt, TextView.BufferType.SPANNABLE);
            subjectTextView.setLineSpacing(0, 1);
            textParams.setMargins(5, 0, 0, 0);
            subjectTextView.setLayoutParams(textParams);
        }


        if (currentLesson.getLessonName().equals("Break")) {
            container.setBackgroundColor(Color.rgb(51,181,229));
            contParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());


            subjectContParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
            subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);



        }
        if(currentLesson.getLessonName().equals("Lunch")){
            container.setBackgroundColor(Color.rgb(51,181,229));
        }





        return itemView;
    }
}