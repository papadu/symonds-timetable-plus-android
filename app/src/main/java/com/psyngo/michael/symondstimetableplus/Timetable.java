package com.psyngo.michael.symondstimetableplus;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Timetable extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    static List<Lesson> Week;
    static List<Lesson> Monday;
    static List<Lesson> Tuesday;
    static List<Lesson> Wednesday;
    static List<Lesson> Thursday;
    static List<Lesson> Friday;

    TextView _tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Bundle extras = getIntent().getExtras();
        String myExtra = extras.getString("timetableHTML");
        parseHTML(myExtra);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Thread myThread = null;

        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }



    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{




                    Calendar calendar = Calendar.getInstance();
                    int dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                    List<Lesson> todaysLessons = null;

                    switch (dayNum){

                        case 1: todaysLessons = Monday;
                            break;
                        case 2: todaysLessons = Tuesday;
                            break;
                        case 3: todaysLessons = Wednesday;
                            break;
                        case 4: todaysLessons = Thursday;
                            break;
                        case 5: todaysLessons = Friday;
                            break;


                    }





                    for (Lesson les: todaysLessons){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.set(Calendar.DAY_OF_MONTH, les.getEndTime().get(Calendar.DAY_OF_MONTH));
                        cal.set(Calendar.MONTH, les.getEndTime().get(Calendar.MONTH));
                        cal.set(Calendar.YEAR, les.getEndTime().get(Calendar.YEAR));
                        TextView quickviewTitle = (TextView) findViewById(R.id.subject_textview);
                        TextView timelefttextview = (TextView) findViewById(R.id.time_left_texview);
                        TextView prefixtextview = (TextView) findViewById(R.id.prefix_textview);

                        quickviewTitle.setText("No more Lessons today");
                        prefixtextview.setText("");
                        timelefttextview.setText("");


                        Date dt = cal.getTime();
                        long currenttime = dt.getTime();
                        if (currenttime > les.getStartTime().getTime().getTime() && currenttime < les.getEndTime().getTime().getTime()){

                            Log.d("myapp", les.getEndTime().getTime().toString());
                            Log.d("myapp", cal.getTime().toString());

                            String timeleft = String.valueOf(Math.abs(les.getEndTime().getTimeInMillis() - cal.getTimeInMillis())/60000);
                            timelefttextview.setText(timeleft + " Minutes left");
                            quickviewTitle.setText(les.getLessonName());

                            prefixtextview.setText("Happening now");
                            break;

                        }
                        else if (les.getLessonName().equals("Break") != true && currenttime < les.getStartTime().getTime().getTime() && les.getLessonName().equals("Study Period") != true ){


                            quickviewTitle.setText(les.getLessonName());
                            long timeleft = Math.abs(les.getStartTime().getTimeInMillis() - cal.getTimeInMillis())/60000;
                            if(timeleft < 60){
                                String timeleftminutes = String.valueOf(timeleft);
                                timelefttextview.setText("in " + timeleftminutes + " Minutes");
                            }
                            else{
                                String timeleftminutes = String.valueOf(timeleft % 60);
                                String timelefthours = String.valueOf((timeleft-(timeleft%60))/60) + " Hours";
                                timelefttextview.setText("in " + timelefthours + " and " + timeleftminutes + " Minutes");
                            }

                            prefixtextview.setText("Your next lesson is");


                            break;



                        }

                    }





                    ;
                }catch (Exception e) {}
            }
        });
    }

    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(10000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }


    public void parseHTML(String timetableHTMLstring){
        Document doc = Jsoup.parse(timetableHTMLstring);
        Element table = doc.select("#Timetable").get(0);

        Elements rows = table.select("tr");

        String length = "";
        String subject= "";
        String teacher = "";
        String room = "";
        String time = "";
        Date startTime;
        Date endTime;

        Week = new ArrayList<Lesson>();
        Monday = new ArrayList<Lesson>();
        Tuesday = new ArrayList<Lesson>();
        Wednesday = new ArrayList<Lesson>();
        Thursday = new ArrayList<Lesson>();
        Friday = new ArrayList<Lesson>();


        int[] offsets = new int[rows.size()];

        for (int i = 1; i < rows.get(0).children().size(); i++) //unless colspans are used, this should return the number of columns
        {
            for (int j = 1; j < rows.size(); j++) // loops through the rows of each column
                 {
                         Element cell = rows.get(j).child(i + offsets[j]); //get an individual cell

            if (cell.hasAttr("rowspan")) //if that cell has a rowspan
            {
                int rowspan = Integer.parseInt(cell.attr("rowspan"));

                for (int k = 1; k < rowspan; k++)
                {
                    offsets[j + k]--; //add offsets to rows that now have a cell "missing"
                }

                j += rowspan - 1; //add rowspan to index, to skip the "missing" cells
            }

            if (cell.hasClass("lesson") || cell.hasClass("activity") || cell.hasClass("tutorgroup")){
                subject = cell.select(".title").text();
                teacher = cell.select(".subtitle").text();
                String[] split = cell.select(".room").text().split(" ");
                room = split[0];
                time = split[1];
                length = cell.attr("rowspan");


            }
            else //if (cell.hasClass("blank"))//
            {
                subject = cell.text();
                length = "1";

                teacher = "";
                room = "";
                time = "";

            }

                     Calendar endcal =Calendar.getInstance();
                     Calendar startcal = Calendar.getInstance();


                     if (cell.hasClass("lesson")) {


                         try {
                             startcal.setTime(new SimpleDateFormat("hh:mmaa").parse(time));

                             endcal.setTime(startcal.getTime());
                             endcal.add(Calendar.MINUTE, 55 * Integer.parseInt(length));

                         } catch (ParseException e) {
                             e.printStackTrace();
                         }
                     }

                     if (cell.hasClass("tutorgroup")) {


                         try {
                             startcal.setTime(new SimpleDateFormat("hh:mmaa").parse(time));

                             endcal.setTime(startcal.getTime());
                             endcal.add(Calendar.MINUTE, 30 * Integer.parseInt(length));

                         } catch (ParseException e) {
                             e.printStackTrace();
                         }
                     }

                     if (cell.hasClass("activity")) {
                         if(j==7){
                             try {
                                 startcal.setTime(new SimpleDateFormat("hh:mmaa").parse(time));

                                 endcal.setTime(startcal.getTime());
                                 endcal.add(Calendar.MINUTE, 50 * Integer.parseInt(length));

                             } catch (ParseException e) {
                                 e.printStackTrace();
                             }
                         }
                         else {
                             try {
                                 startcal.setTime(new SimpleDateFormat("hh:mmaa").parse(time));

                                 endcal.setTime(startcal.getTime());
                                 endcal.add(Calendar.MINUTE, 55 * Integer.parseInt(length));

                             } catch (ParseException e) {
                                 e.printStackTrace();
                             }
                         }


                     }








                     Lesson lesson = new Lesson(time, subject, teacher, room, length, startcal, endcal);

                    Week.add(lesson);

                 }






        }
        int day = 1;
        int count = 0;
        for (Lesson les : Week){
            switch(day) {
                case 1: Monday.add(les);
                    break;
                case 2: Tuesday.add(les);
                    break;
                case 3: Wednesday.add(les);
                    break;
                case 4: Thursday.add(les);
                    break;
                case 5: Friday.add(les);
                    break;
            }
            count += Integer.parseInt(les.getLength());
            if (count == 10){
                day += 1;
                count = 0;
            }

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, TimetableFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = "Tuesday";
                break;
            case 3:
                mTitle = "Wednesday";
                break;
            case 4:
                mTitle = "Thursday";
                break;
            case 5:
                mTitle = "Friday";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.timetable, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TimetableFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static boolean openCurrentDay = true;



        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TimetableFragment newInstance(int sectionNumber) {
            TimetableFragment fragment = new TimetableFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);



            return fragment;
        }

        public TimetableFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
            Bundle args = getArguments();
            int dayNum;
            if(openCurrentDay == true){
                Calendar calendar = Calendar.getInstance();
                dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if(dayNum > 5 || dayNum == 0){
                    dayNum = 1;
                }
                openCurrentDay = false;

            }
            else {
                dayNum = args.getInt(ARG_SECTION_NUMBER);
            }
            ArrayAdapter<Lesson> adapter = null;
            TextView dayHeader = (TextView) rootView.findViewById(R.id.day_textview);
            switch (dayNum){
                case 1: adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Monday);
                    dayHeader.setText("MONDAY");
                    break;
                case 2: adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Tuesday);
                    dayHeader.setText("TUESDAY");
                    break;
                case 3: adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Wednesday);
                    dayHeader.setText("WEDNESDAY");
                    break;
                case 4: adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Thursday);
                    dayHeader.setText("THURSDAY");
                    break;
                case 5: adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Friday);
                    dayHeader.setText("FRIDAY");
                    break;

            }
            SetUpQuickView(rootView);

            ListView list = (ListView) rootView.findViewById(R.id.timetable_listview);
            list.setAdapter(adapter);
            LinearLayout qv = (LinearLayout) rootView.findViewById(R.id.Quick_view);
            qv.setBackgroundColor(Color .rgb(28,168,244));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Timetable) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        private void SetUpQuickView(View rootView){
            Calendar calendar = Calendar.getInstance();
            int dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            List<Lesson> todaysLessons = null;

            switch (dayNum){

                case 1: todaysLessons = Monday;
                    break;
                case 2: todaysLessons = Tuesday;
                    break;
                case 3: todaysLessons = Wednesday;
                    break;
                case 4: todaysLessons = Thursday;
                    break;
                case 5: todaysLessons = Friday;
                    break;


            }
            for (Lesson les: todaysLessons){
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.DAY_OF_MONTH, les.getEndTime().get(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.MONTH, les.getEndTime().get(Calendar.MONTH));
                cal.set(Calendar.YEAR, les.getEndTime().get(Calendar.YEAR));


                Date dt = cal.getTime();
                long currenttime = dt.getTime();
                if (currenttime > les.getStartTime().getTime().getTime() && currenttime < les.getEndTime().getTime().getTime()){
                    TextView quickviewTitle = (TextView) rootView.findViewById(R.id.subject_textview);
                    TextView timelefttextview = (TextView) rootView.findViewById(R.id.time_left_texview);
                    Log.d("myapp", les.getEndTime().getTime().toString());
                    Log.d("myapp", cal.getTime().toString());

                    String timeleft = String.valueOf(Math.abs(les.getEndTime().getTimeInMillis() - cal.getTimeInMillis())/60000);
                    timelefttextview.setText(timeleft + " Minutes left");
                    quickviewTitle.setText(les.getLessonName());
                    TextView prefixtextview = (TextView) rootView.findViewById(R.id.prefix_textview);
                    prefixtextview.setText("Happening now");
                    break;

                }
                else if (les.getLessonName().equals("Break") != true && currenttime < les.getStartTime().getTime().getTime() && les.getLessonName().equals("Study Period") != true ){
                    TextView quickviewTitle = (TextView) rootView.findViewById(R.id.subject_textview);
                    TextView timelefttextview = (TextView) rootView.findViewById(R.id.time_left_texview);
                    TextView prefixtextview = (TextView) rootView.findViewById(R.id.prefix_textview);
                    quickviewTitle.setText(les.getLessonName());
                    long timeleft = Math.abs(les.getStartTime().getTimeInMillis() - cal.getTimeInMillis())/60000;
                    if(timeleft < 60){
                        String timeleftminutes = String.valueOf(timeleft);
                        timelefttextview.setText("in " + timeleftminutes + " Minutes");
                    }
                    else{
                        String timeleftminutes = String.valueOf(timeleft % 60);
                        String timelefthours = String.valueOf((timeleft-(timeleft%60))/60) + " Hours";
                        timelefttextview.setText("in " + timelefthours + " and " + timeleftminutes + " Minutes");
                    }

                    prefixtextview.setText("Your next lesson is");


                    break;



                }

            }
        }



    }



}
