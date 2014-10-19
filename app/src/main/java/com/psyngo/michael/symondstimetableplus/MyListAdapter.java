package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
            subjectTextView.setText(currentLesson.getLessonName());
        roomTextView.setText(currentLesson.getLessonRoom());
        timeTextView.setText(currentLesson.getLessonTime());
        teacherTextView.setText(currentLesson.getLessonTeacher());
        ImageView i = (ImageView) itemView.findViewById(R.id.side_colour);

        if(currentLesson.getLessonName().equals("Free Period")){

            i.setBackgroundColor(Color.rgb(39, 174, 96));
            timeTextView.setVisibility(View.GONE);

        }
        else {
            i.setBackgroundColor(Color.rgb(28,168,244));
            timeTextView.setVisibility(View.VISIBLE);

        }






        return itemView;
    }
}