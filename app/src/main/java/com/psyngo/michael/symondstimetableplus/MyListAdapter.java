package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        if (itemView == null){


                itemView = mInflater.inflate(R.layout.list_item_lesson, parent, false);


        }



            Lesson currentLesson = objects.get(position);




        TextView subjectTextView = (TextView) itemView.findViewById(R.id.list_item_subject_textview);
        TextView timeTextView = (TextView) itemView.findViewById(R.id.list_item_time_textview);
        TextView roomTextView = (TextView) itemView.findViewById(R.id.list_item_room_textview);
        TextView teacherTextView = (TextView) itemView.findViewById(R.id.list_item_teacher_textview);
        ImageView i = (ImageView) itemView.findViewById(R.id.side_colour);
        LinearLayout container = (LinearLayout) itemView.findViewById(R.id.list_item_container);

        ViewGroup.LayoutParams contParams = container.getLayoutParams();

        i.setBackgroundColor(Color.rgb(28,168,244));
        timeTextView.setVisibility(View.VISIBLE);
        teacherTextView.setVisibility(View.VISIBLE);
        roomTextView.setVisibility(View.VISIBLE);

        subjectTextView.setText(currentLesson.getLessonName());
        roomTextView.setText(currentLesson.getLessonRoom());
        timeTextView.setText(currentLesson.getLessonTime());
        teacherTextView.setText(currentLesson.getLessonTeacher());

        subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        subjectTextView.setTextColor(Color.rgb(126,128,125));

        contParams.height = 72;

        if(currentLesson.getLessonName().equals("Study Period")){

            i.setBackgroundColor(Color.rgb(39, 174, 96));
            subjectTextView.setText("Free Period");
            timeTextView.setVisibility(View.GONE);
            teacherTextView.setText("");
            roomTextView.setText("");
            subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            //subjectTextView.setTextColor(Color.rgb(216,217,217));

        }
        if(currentLesson.getLessonName().equals("Lunch")){
            i.setBackgroundColor(Color.rgb(39, 174, 96));
            subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            //subjectTextView.setTextColor(Color.rgb(216,217,217));
            timeTextView.setVisibility(View.GONE);
            teacherTextView.setText("");
            roomTextView.setText("");

        }
        if (currentLesson.getLessonName().equals("Symonds Lecture Programme")){
            subjectTextView.setText("Lecture Programme");
            teacherTextView.setVisibility(View.GONE);
        }

        if(currentLesson.getLength().equals("2")){
            contParams.height = 144;
        }

        if(currentLesson.getLessonName().equals("Break")){
            teacherTextView.setVisibility(View.GONE);
            timeTextView.setVisibility(View.GONE);
            roomTextView.setVisibility(View.GONE);
            contParams.height = 24;
            subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            i.setBackgroundColor(Color.rgb(39, 174, 96));
        }

        if (currentLesson.getLessonName().equals("Tutor Group")){
            subjectTextView.setText("Tutor");
            timeTextView.setVisibility(View.GONE);
            contParams.height = 36;
        }
        if(position < objects.size()-1) {
            if (objects.get(position + 1).getLessonName().equals("Lunch") || objects.get(position + 1).getLessonTime().equals("1:00pm")) {
                contParams.height = 36;
            }
        }











        return itemView;
    }
}