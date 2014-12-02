package com.psyngo.michael.symondstimetableplus;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Lesson x = Timetable.clickedLesson;
        TextView time = (TextView) findViewById(R.id.detail_timetextview);
        TextView subject = (TextView) findViewById(R.id.detail_subjecttextview);
        TextView room = (TextView) findViewById(R.id.detail_roomtextview);
        TextView teacher = (TextView) findViewById(R.id.detail_teacherteactview);

        time.setText(x.getLessonTime());
        subject.setText(x.getLessonName());
        room.setText(x.getLessonRoom());
        teacher.setText(x.getLessonTeacher());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
