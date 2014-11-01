package com.psyngo.michael.symondstimetableplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    static boolean happeningNow = true;
    static String happeningNowprefixText = "Happening now";
    static String happeningNowsubjectText;
    static String happeningNowsubtitleText;
    static String nextLessonprefixText = "Your next lesson is";
    static String NextLessonsubjectText;
    static String NextLessonsubtitleText;


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
        myThread = new Thread(myRunnableThread);
        myThread.start();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                Quickview quickview = new Quickview(findViewById(android.R.id.content));
                quickview.updateQuickview();
            }
        });
    }


    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(10000); // Pause of 10 Seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void animateQuickview(final View view){
        final LinearLayout quickview = (LinearLayout)  view.findViewById(R.id.Quick_view);
        final TextView topTextview = (TextView) view.findViewById(R.id.prefix_textview);
        final TextView middleTextview = (TextView) view.findViewById(R.id.subject_textview);
        final TextView bottomTextview = (TextView) view.findViewById(R.id.time_left_texview);
        final String topText;
        final String middleText;
        final String bottomText;

        quickview.setClickable(false);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        if(happeningNow){
            topText = nextLessonprefixText;
            middleText = NextLessonsubjectText;
            bottomText = NextLessonsubtitleText;


        }
        else{
            topText = happeningNowprefixText;
            middleText = happeningNowsubjectText;
            bottomText = happeningNowsubtitleText;

        }


        ValueAnimator topExit = getAnimation(16, -300, true, topTextview, view, width, topText);
        ValueAnimator middleExit = getAnimation(16, -300, true, middleTextview, view, width, middleText);
        ValueAnimator bottomExit = getAnimation(16, -300, true, bottomTextview, view, width, bottomText);

        ValueAnimator topReturn = getAnimation(-300, 16, false, topTextview, view, width, topText);
        ValueAnimator middleReturn = getAnimation(-300, 16, false, middleTextview, view, width, middleText);
        ValueAnimator bottomReturn = getAnimation(-300, 16, false, bottomTextview, view, width, bottomText);

        AnimatorSet textviews = new AnimatorSet();
        textviews.play(topExit).before(middleExit);
        textviews.play(middleExit).before(bottomExit);
        textviews.play(bottomExit);
        textviews.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                happeningNow = !happeningNow;

            }
        });


        AnimatorSet textviewReturn = new AnimatorSet();
        textviewReturn.play(topReturn).before(middleReturn);
        textviewReturn.play(middleReturn).before(bottomReturn);
        textviewReturn.play(bottomReturn);

        AnimatorSet fullAnimation = new AnimatorSet();
        fullAnimation.play(textviews).before(textviewReturn);
        fullAnimation.play(textviewReturn);
        fullAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                quickview.setClickable(true);

            }
        });
        fullAnimation.start();






    }

    public ValueAnimator getAnimation(int startMargin, int endMargin, boolean exitAnimation, final TextView textview, final View view, final int screenwidth, final String newText){
        ValueAnimator valAnim = ValueAnimator.ofInt(startMargin, endMargin);
        valAnim.setDuration(200);


        valAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (Integer) animation.getAnimatedValue(), view.getContext().getResources().getDisplayMetrics()), 0, 0, 0);
                lp.width = screenwidth;
                textview.setLayoutParams(lp);

            }


        });

        if(exitAnimation){
            valAnim.setInterpolator(new AccelerateInterpolator());

        }
        else{
            valAnim.setInterpolator(new DecelerateInterpolator());
            valAnim.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    textview.setText(newText);

                }
            });
        }

        return valAnim;

    }


    public void parseHTML(String timetableHTMLstring) {
        Document doc = Jsoup.parse(timetableHTMLstring);
        Element table = doc.select("#Timetable").get(0);

        Elements rows = table.select("tr");

        int length;
        String subject;
        String teacher;
        String room;
        String time;
        String nexttime = "";
        String whoElseFree = "";
        int subjectColor = Color.rgb(22,168,158);
        int colorIndex = 0;
        int[] subjectColors = new int[]{Color.rgb(24,212,179), Color.rgb(0,191,165),Color.rgb(13,182,159)};
        String[] times = new String[]{"08:30", "09:25", "10:20", "10:40", "11:35", "12:30", "13:00", "13:50", "14:45", "15:40", "16:35"};
        boolean useFreeHighlight = true;
        int lessonIndex = 0;


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

                    for (int k = 1; k < rowspan; k++) {
                        offsets[j + k]--; //add offsets to rows that now have a cell "missing"
                    }

                    j += rowspan - 1; //add rowspan to index, to skip the "missing" cells
                }

                if (cell.hasClass("lesson") || cell.hasClass("activity") || cell.hasClass("tutorgroup")) {
                    subject = cell.select(".title").text();
                    teacher = cell.select(".subtitle").text();
                    String[] split = cell.select(".room").text().split(" ");
                    room = split[0];
                    time = times[lessonIndex];
                    length = Integer.parseInt(cell.attr("rowspan"));

                    if(colorIndex > subjectColors.length-1){
                        colorIndex = 0;
                    }

                    if(time.equals("08:30")){
                        colorIndex = 0;
                    }

                    subjectColor = subjectColors[colorIndex];
                    colorIndex += 1;





                } else //if (cell.hasClass("blank"))//
                {
                    subject = cell.text();
                    length = 1;
                    colorIndex = 0;
                    if(!subject.equals("Break") && !subject.equals("Lunch")) {
                        if (useFreeHighlight) {
                            subjectColor = Color.rgb(19, 162, 215);
                            useFreeHighlight = false;
                        } else {
                            subjectColor = Color.rgb(0, 153, 204);
                            useFreeHighlight = true;
                        }
                    }


                    teacher = "";
                    room = "";
                    time = times[lessonIndex];

                }

                if (rows.size() == 10 && rows.size() - 1 == j) {

                        length = 2;

                }

                if(subject.equals("Study Period")){
                    subject = "Free Period";
                }

                if(subject.equals("Symonds Lecture Programme")){
                    subject = "Lecture Programme";
                }

                if(length == 2){
                    nexttime = times[lessonIndex + 1];
                    time = times[lessonIndex];
                }


                Calendar endcal = Calendar.getInstance();
                Calendar startcal = Calendar.getInstance();

                try {
                    startcal.setTime(new SimpleDateFormat("HH:mm").parse(time));
                    endcal.setTime(new SimpleDateFormat("HH:mm").parse(times[lessonIndex + length]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (length == 2 && subject.equals("Free Period")){
                    try {
                        startcal.setTime(new SimpleDateFormat("HH:mm").parse(time));
                        endcal.setTime(new SimpleDateFormat("HH:mm").parse(nexttime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Week.add(new Lesson(time, subject, teacher, room, 1, nexttime, whoElseFree, startcal, endcal, subjectColor));
                try {
                    startcal.setTime(new SimpleDateFormat("HH:mm").parse(nexttime));
                    endcal.setTime(new SimpleDateFormat("HH:mm").parse(times[lessonIndex + length]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                    if (useFreeHighlight) {
                        subjectColor = Color.rgb(19, 162, 215);
                        useFreeHighlight = false;
                    } else {
                        subjectColor = Color.rgb(0, 153, 204);
                        useFreeHighlight = true;
                    }
                Week.add(new Lesson(nexttime, subject, teacher, room, 1, nexttime, whoElseFree, startcal, endcal, subjectColor));
                }
                else {
                    Lesson lesson = new Lesson(time, subject, teacher, room, length, nexttime, whoElseFree, startcal, endcal, subjectColor);

                    Week.add(lesson);
                }
                lessonIndex += length;
                if(lessonIndex>9){
                    lessonIndex=0;
                }

            }


        }
        int day = 1;
        int count = 0;
        for (Lesson les : Week) {
            switch (day) {
                case 1:
                    Monday.add(les);
                    break;
                case 2:
                    Tuesday.add(les);
                    break;
                case 3:
                    Wednesday.add(les);
                    break;
                case 4:
                    Thursday.add(les);
                    break;
                case 5:
                    Friday.add(les);
                    break;
            }
            count += les.getLength();
            if (count == 10) {
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

            Typeface robotoThin = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            TextView subtitle = (TextView) rootView.findViewById(R.id.time_left_texview);
            TextView subject = (TextView) rootView.findViewById(R.id.subject_textview);
            TextView prefix = (TextView) rootView.findViewById(R.id.prefix_textview);

            prefix.setTypeface(robotoThin);
            //subject.setTypeface(robotoThin);
            subtitle.setTypeface(robotoThin);

            int dayNum;

             dayNum = args.getInt(ARG_SECTION_NUMBER);

            ArrayAdapter<Lesson> adapter = null;
            TextView dayHeader = (TextView) rootView.findViewById(R.id.day_textview);
            switch (dayNum) {
                case 1:
                    adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Monday);
                    dayHeader.setText("MONDAY");
                    break;
                case 2:
                    adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Tuesday);
                    dayHeader.setText("TUESDAY");
                    break;
                case 3:
                    adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Wednesday);
                    dayHeader.setText("WEDNESDAY");
                    break;
                case 4:
                    adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Thursday);
                    dayHeader.setText("THURSDAY");
                    break;
                case 5:
                    adapter = new MyListAdapter(getActivity().getApplicationContext(), R.layout.list_item_lesson, Friday);
                    dayHeader.setText("FRIDAY");
                    break;

            }
            Quickview quickview = new Quickview(rootView);
            quickview.updateQuickview();
            ListView list = (ListView) rootView.findViewById(R.id.timetable_listview);
            list.setAdapter(adapter);
            LinearLayout qv = (LinearLayout) rootView.findViewById(R.id.Quick_view);
            qv.setBackgroundColor(Color.rgb(51,181,229));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Timetable) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }
}

class Quickview {
    View rootView;

    public Quickview(View rootView) {
        this.rootView = rootView;
    }

    public void updateQuickview() {

        TextView middleTextView = (TextView) rootView.findViewById(R.id.subject_textview);
        TextView bottomTextView = (TextView) rootView.findViewById(R.id.time_left_texview);
        TextView topTextView = (TextView) rootView.findViewById(R.id.prefix_textview);
        ImageView divider = (ImageView) rootView.findViewById(R.id.divider_imageview);


        Calendar calendar = Calendar.getInstance();
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        List<Lesson> todaysLessons = null;
        if (dayNum > 0 && dayNum < 6) {
            switch (dayNum) {

                case 1:
                    todaysLessons = Timetable.Monday;
                    break;
                case 2:
                    todaysLessons = Timetable.Tuesday;
                    break;
                case 3:
                    todaysLessons = Timetable.Wednesday;
                    break;
                case 4:
                    todaysLessons = Timetable.Thursday;
                    break;
                case 5:
                    todaysLessons = Timetable.Friday;
                    break;


            }

            middleTextView.setVisibility(View.VISIBLE);
            bottomTextView.setVisibility(View.VISIBLE);
            topTextView.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);


            for (Lesson les : todaysLessons) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.DAY_OF_MONTH, les.getEndTime().get(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.MONTH, les.getEndTime().get(Calendar.MONTH));
                cal.set(Calendar.YEAR, les.getEndTime().get(Calendar.YEAR));





                Date dt = cal.getTime();
                long currenttime = dt.getTime();
                if (currenttime > les.getStartTime().getTime().getTime() && currenttime < les.getEndTime().getTime().getTime()) {


                    String timeleft = String.valueOf(1+(Math.abs(les.getEndTime().getTimeInMillis() - cal.getTimeInMillis()) / 60000));


                    Timetable.happeningNowsubjectText = les.getLessonName();
                    if(les.getLessonName().equals("Free Period")){
                        Timetable.happeningNowsubtitleText = "With Mckenzie, Elliot and 5 others";

                    }
                    else {
                        Timetable.happeningNowsubtitleText = timeleft + " Minutes left";
                    }

                } else if (!les.getLessonName().equals("Break")&& currenttime < les.getStartTime().getTime().getTime() && !les.getLessonName().equals("Free Period")&& !les.getLessonName().equals("Lunch")) {


                    Timetable.NextLessonsubjectText = les.getLessonName();
                    long timeleft = 1+(Math.abs(les.getStartTime().getTimeInMillis() - cal.getTimeInMillis()) / 60000);
                    if (timeleft < 60) {
                        String timeleftminutes = String.valueOf(timeleft);
                        Timetable.NextLessonsubtitleText = ("in " + timeleftminutes + " Minutes");
                    } else {
                        String timeleftminutes = String.valueOf(timeleft % 60);
                        String timelefthours = String.valueOf((timeleft - (timeleft % 60)) / 60) + " Hours";
                        Timetable.NextLessonsubtitleText = ("in " + timelefthours + " and " + timeleftminutes + " Minutes");
                    }




                    break;


                }

            }

            if(Timetable.happeningNow){
                topTextView.setText(Timetable.happeningNowprefixText);
                middleTextView.setText(Timetable.happeningNowsubjectText);
                bottomTextView.setText(Timetable.happeningNowsubtitleText);
            }
            else{
                topTextView.setText(Timetable.nextLessonprefixText);
                middleTextView.setText(Timetable.NextLessonsubjectText);
                bottomTextView.setText(Timetable.NextLessonsubtitleText);
            }
        } else {
            middleTextView.setVisibility(View.GONE);
            bottomTextView.setVisibility(View.GONE);
            topTextView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }


        ;


    }


}
