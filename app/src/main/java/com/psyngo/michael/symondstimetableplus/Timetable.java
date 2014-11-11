package com.psyngo.michael.symondstimetableplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvMetadata;
import io.orchestrate.client.OrchestrateClient;
import io.orchestrate.client.ResponseAdapter;


public class Timetable extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

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
    static String happeningNowsubjectText = "";
    static String happeningNowsubtitleText = "";
    static String nextLessonprefixText = "Your next lesson is";
    static String NextLessonsubjectText = "";
    static String NextLessonsubtitleText = "";

    static List<String> times = new ArrayList<String>(Arrays.asList("08:30", "09:25", "10:20", "10:40", "11:35", "12:30", "13:00", "13:50", "14:45", "15:40", "16:35"));


    static List<String> lessontimes = new ArrayList<String>(Arrays.asList("08:30"));

    static List<Lesson> todaysLessons = null;

    static Lesson clickedLesson;


    static boolean started = false;

    static View root;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        if(!started) {
            Bundle extras = getIntent().getExtras();
            String myExtra = extras.getString("timetableHTML");
            parseHTML(myExtra);
            started=true;
        }

        addFriends();

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

    public void addFriends(){
        Monday = deleteFrees(Monday);
        Tuesday = deleteFrees(Tuesday);
        Wednesday = deleteFrees(Wednesday);
        Thursday = deleteFrees(Thursday);
        Friday = deleteFrees(Friday);

        for(FriendList fl : AddAFriend_Activity.friends){
            Monday = addFriendsFreePeriods(Monday, fl.getValue().getMonday(), fl.getKey());
            Tuesday = addFriendsFreePeriods(Tuesday, fl.getValue().getTuesday(), fl.getKey());
            Wednesday = addFriendsFreePeriods(Wednesday, fl.getValue().getWednesday(), fl.getKey());
            Thursday = addFriendsFreePeriods(Thursday, fl.getValue().getThursday(), fl.getKey());
            Friday = addFriendsFreePeriods(Friday, fl.getValue().getFriday(), fl.getKey());
        }

    }

    public List<Lesson> addFriendsFreePeriods(List<Lesson> day, List<Calendar[]> friendsDay, String name){
        for (Lesson les: day){

            for(Calendar[] free : friendsDay) {
                if (les.getLessonName().equals("Free Period") && les.getStartTime().getTime().getTime() >= free[0].getTime().getTime() && les.getEndTime().getTime().getTime() <= free[1].getTime().getTime()){
                    List<String> whosFree = les.getWhoElseFree();
                    whosFree.add(name);
                    les.setWhoElseFree(whosFree);

                }
            }
        }

        return day;
    }

    public List<Lesson> deleteFrees(List<Lesson> day){
        for (Lesson les: day){
            les.setWhoElseFree(new ArrayList<String>());
        }
        return day;
    }




    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                Quickview quickview = new Quickview(root);
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

        List<String> whoElseFree = new ArrayList<String>();
        int subjectColor = Color.rgb(22,168,158);
        int colorIndex = 0;
        int[] subjectColors = new int[]{Color.rgb(24,212,179), Color.rgb(0,191,165),Color.rgb(13,182,159)};

        boolean useFreeHighlight = true;




        Week = new ArrayList<Lesson>();
        Monday = new ArrayList<Lesson>();
        Tuesday = new ArrayList<Lesson>();
        Wednesday = new ArrayList<Lesson>();
        Thursday = new ArrayList<Lesson>();
        Friday = new ArrayList<Lesson>();

        List<Calendar[]> mondayFrees = new ArrayList<Calendar[]>();
        List<Calendar[]> tuesdayFrees = new ArrayList<Calendar[]>();
        List<Calendar[]> wednesdayFrees = new ArrayList<Calendar[]>();
        List<Calendar[]> thursdayFrees = new ArrayList<Calendar[]>();
        List<Calendar[]> fridayFrees = new ArrayList<Calendar[]>();

        List<Lesson> TodaysLessons = new ArrayList<Lesson>();

        int day = 1;




        int[] offsets = new int[rows.size()];

        for (int i = 0; i < rows.get(0).children().size(); i++) //unless colspans are used, this should return the number of columns
        {

            for (int j = 1; j < rows.size(); j++) // loops through the rows of each column
            {

                Element cell = rows.get(j).child(i + offsets[j]); //get an individual cell
                if(cell.hasClass("time")){
                    lessontimes.add(cell.text());




                    continue;
                }



                //Log.d("myapp", "j:" + j + "lessonIndex: " + lessonIndex);


                int rowspan;
                if (cell.hasAttr("rowspan")) //if that cell has a rowspan
                {
                    rowspan = Integer.parseInt(cell.attr("rowspan"));

                    for (int k = 1; k < rowspan; k++) {
                        offsets[j + k]--; //add offsets to rows that now have a cell "missing"
                    }

                    j += rowspan - 1; //add rowspan to index, to skip the "missing" cells
                }
                else{
                    rowspan = 1;
                }

                time = lessontimes.get(j-rowspan);

                if (cell.hasClass("item")) {
                    subject = cell.select(".title").text();
                    teacher = cell.select(".subtitle").text();
                    String[] split = cell.select(".room").text().split(" ");
                    room = split[0];



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


                }



                if(subject.equals("Study Period")){
                    subject = "Free Period";
                }

                if(subject.equals("Symonds Lecture Programme")){
                    subject = "Lecture Programme";
                }


                Calendar endcal = Calendar.getInstance();
                Calendar startcal = Calendar.getInstance();

                Log.d("myapp", subject + ": " + time + " ->" + lessontimes.get(j) + "j == " + j);

                try {
                    startcal.setTime(new SimpleDateFormat("HH:mm").parse(time));
                    endcal.setTime(new SimpleDateFormat("HH:mm").parse(lessontimes.get(j)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                length = (int) Math.abs(startcal.getTimeInMillis() - endcal.getTimeInMillis()) / 60000;



                Lesson lesson = new Lesson(time, subject, teacher, room, length, whoElseFree, startcal, endcal, subjectColor);



                switch (day) {
                    case 1:
                        Monday.add(lesson);

                        break;
                    case 2:
                        Tuesday.add(lesson);
                        break;
                    case 3:
                        Wednesday.add(lesson);
                        break;
                    case 4:
                        Thursday.add(lesson);
                        break;
                    case 5:
                        Friday.add(lesson);
                        break;
                }

                if(j >= rows.size()-1){
                    day += 1;
                }


                /*lessonIndex += rowspan;
                if(lessonIndex>rows.size()){
                    lessonIndex=0;
                }*/

            }


        }
        Log.d("myapp", lessontimes.toString());

        Monday = joinFreePeriods(Monday);
        Tuesday = joinFreePeriods(Tuesday);
        Wednesday = joinFreePeriods(Wednesday);
        Thursday = joinFreePeriods(Thursday);
        Friday = joinFreePeriods(Friday);

        mondayFrees = getFrees(Monday);
        tuesdayFrees = getFrees(Tuesday);
        wednesdayFrees = getFrees(Wednesday);
        thursdayFrees = getFrees(Thursday);
        fridayFrees = getFrees(Friday);

        FriendDatabaseObject value = new FriendDatabaseObject(mondayFrees,tuesdayFrees,wednesdayFrees,thursdayFrees,fridayFrees);
        String key = doc.select("#content").text().split(",")[0];
        Log.d("myapp", value.toString());
        addToServer ats = new addToServer(key, value, getApplicationContext());
        ats.execute();





    }

    public List<Lesson> joinFreePeriods(List<Lesson> dayofweek){

        for (int i = 1; i < dayofweek.size(); i++){
            Lesson currentLesson = dayofweek.get(i);
            Lesson previousLesson = dayofweek.get(i-1);
            if(currentLesson.getLessonName().equals("Free Period") && previousLesson.getLessonName().equals("Free Period") && currentLesson.getLength() + previousLesson.getLength() <= 55){
                dayofweek.remove(i);
                previousLesson.setLength(currentLesson.getLength() + previousLesson.getLength());
                previousLesson.setEndTime(currentLesson.getEndTime());
                dayofweek.set(i-1, previousLesson);
                i-=1;
            }
        }
        return dayofweek;
    }

    public List<Calendar[]> getFrees(List<Lesson> dayofweek){
        List<Calendar[]> x = new ArrayList<Calendar[]>();
        for(Lesson les: dayofweek){
            if(les.getLessonName().equals("Free Period")){
                x.add(new Calendar[]{les.getStartTime(), les.getEndTime()});
            }
        }
        return x;
    }

    public void startDetailActivity(View v){
        LinearLayout cont = (LinearLayout) v.findViewById(R.id.cont);
        int i = Integer.parseInt(cont.getTag().toString());
        clickedLesson = todaysLessons.get(i);
        Intent detailIntent;
        if(clickedLesson.getLessonName().equals("Free Period")){
            detailIntent = new Intent(v.getContext(), Detail_FreePeriod_Activity.class);
            for(String s : clickedLesson.getWhoElseFree()) {
                Log.d("xxxxxx", s);
            }

        }
        else{
            detailIntent = new Intent(v.getContext(), DetailActivity.class);
        }

        startActivity(detailIntent);



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
        if (id == R.id.action_add_friend) {
            Intent intent = new Intent(root.getContext(), AddAFriend_Activity.class);
            startActivity(intent);
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

            final View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
            Bundle args = getArguments();

            Typeface robotoLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoThin = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Thin.ttf");
            TextView subtitle = (TextView) rootView.findViewById(R.id.time_left_texview);
            TextView subject = (TextView) rootView.findViewById(R.id.subject_textview);
            TextView prefix = (TextView) rootView.findViewById(R.id.prefix_textview);

            prefix.setTypeface(robotoLight);

            subtitle.setTypeface(robotoLight);

            int dayNum;

             dayNum = args.getInt(ARG_SECTION_NUMBER);


            TextView dayHeader = (TextView) rootView.findViewById(R.id.day_textview);
            switch (dayNum) {
                case 1:
                    todaysLessons = Monday;
                    dayHeader.setText("MONDAY");
                    break;
                case 2:
                    todaysLessons = Tuesday;
                    dayHeader.setText("TUESDAY");
                    break;
                case 3:
                    todaysLessons = Wednesday;
                    dayHeader.setText("WEDNESDAY");
                    break;
                case 4:
                    todaysLessons = Thursday;
                    dayHeader.setText("THURSDAY");
                    break;
                case 5:
                    todaysLessons = Friday;
                    dayHeader.setText("FRIDAY");
                    break;

            }
            root = rootView;

            Quickview quickview = new Quickview(rootView);
            quickview.updateQuickview();
            /*
            final ListView list = (ListView) rootView.findViewById(R.id.timetable_listview);
            list.setAdapter(adapter);
            */


            LinearLayout qv = (LinearLayout) rootView.findViewById(R.id.Quick_view);
            qv.setBackgroundColor(Color.rgb(51,181,229));

            LinearLayout timetableCont = (LinearLayout) rootView.findViewById(R.id.timetable_container);







            for(int i = 0; i < times.size()-1; i++){
                LayoutInflater vi = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.list_item_times, null);

                TextView time = (TextView) v.findViewById(R.id.list_item_time_textview);
                LinearLayout timetextviewcont = (LinearLayout) v.findViewById(R.id.time_textview_container);
                ViewGroup.LayoutParams params = timetextviewcont.getLayoutParams();

                time.setText(times.get(i));
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getActivity().getResources().getDisplayMetrics());

                if(times.get(i).equals("10:20")){
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 24, getActivity().getResources().getDisplayMetrics());
                    time.setText("");
                }
                if(times.get(i).equals("12:30")){
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)72/55*30, getActivity().getResources().getDisplayMetrics());
                    time.setText("");
                }
                if(times.get(i).equals("13:00")){
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)72/55*50, getActivity().getResources().getDisplayMetrics());
                    time.setText(times.get(i));
                }


                time.setTypeface(robotoThin);


                View insertPoint = rootView.findViewById(R.id.times_container);
                ((ViewGroup) insertPoint).addView(v);
            }



            for(Lesson les : todaysLessons){
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

                if(units < 50) {
                    subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, units/55*40);
                }
                else {
                    subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                }
                subjectTextView.setLineSpacing(-10, 1);

                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                textParams.setMargins(5, -10, 0, 0);
                subjectTextView.setLayoutParams(textParams);





                contParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)units/55*72, v.getContext().getResources().getDisplayMetrics());
                if(les.getLength() > 55){
                    units = 55;
                }

                subjectContParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)units/55*72, v.getContext().getResources().getDisplayMetrics());


                if (les.getLessonName().equals("Free Period")){
                    if(les.getLength()>=55) {



                        String sub = "";
                        if(les.getWhoElseFree().size()==0){
                            sub = "Noone.";
                        }
                        if(les.getWhoElseFree().size()==1){
                            sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ".";
                        }
                        if(les.getWhoElseFree().size()==2){
                            sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + " and " + WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + ".";
                        }
                        if(les.getWhoElseFree().size()>2){
                            sub = WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ", " +
                                    WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + "...";
                        }


                        SpannableString txt = new SpannableString("Free Period\n With " + sub);
                        txt.setSpan(new RelativeSizeSpan(0.5f), 11, txt.length(), 0);
                        subjectTextView.setText(txt, TextView.BufferType.SPANNABLE);
                        subjectTextView.setLineSpacing(0, 1);

                        textParams.setMargins(5, 0, 0, 0);

                        subjectTextView.setLayoutParams(textParams);
                    }
                }


                if (les.getLessonName().equals("Break")) {
                    cont.setBackgroundColor(Color.rgb(51,181,229));
                    contParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources().getDisplayMetrics());


                    subjectContParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources().getDisplayMetrics());
                    subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);



                }
                if(les.getLessonName().equals("Lunch")){
                    cont.setBackgroundColor(Color.rgb(51,181,229));

                }


                cont.setTag(todaysLessons.indexOf(les));
                View insertPoint = rootView.findViewById(R.id.timetable_container);
                ((ViewGroup) insertPoint).addView(v);



            }







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
                        String sub = "With ";
                        if(les.getWhoElseFree().size()==0){
                            sub += "Noone.";
                        }
                        if(les.getWhoElseFree().size()==1){
                            sub += WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ".";
                        }
                        if(les.getWhoElseFree().size()==2){
                            sub += WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + " and " + WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + ".";
                        }
                        if(les.getWhoElseFree().size()>2){
                            sub += WordUtils.capitalizeFully(les.getWhoElseFree().get(0).split(" ")[0]) + ", " +
                                    WordUtils.capitalizeFully(les.getWhoElseFree().get(1).split(" ")[0]) + " and " +
                                    (les.getWhoElseFree().size() - 2) + " others.";
                        }
                        Timetable.happeningNowsubtitleText = sub;

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
            if(Timetable.happeningNowsubjectText.equals("") && Timetable.NextLessonsubjectText.equals("")){
                Timetable.happeningNowsubjectText = "Nothing.";
                Timetable.happeningNowsubtitleText = "You're finished for the day.";
                Timetable.NextLessonsubjectText = "Nothing.";
                Timetable.NextLessonsubtitleText = "You're finished for the day.";
            }
            else{
                if(Timetable.happeningNowsubjectText.equals("")){
                    Timetable.happeningNowsubjectText = "Nothing.";
                    Timetable.happeningNowsubtitleText = "College hasn't started yet.";
                }
                if(Timetable.NextLessonsubjectText.equals("")){
                    Timetable.NextLessonsubjectText = "Nothing.";
                    Timetable.NextLessonsubtitleText = "You're finished for the day.";
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

class addToServer extends AsyncTask<Void, Void, Void> {
    FriendDatabaseObject y;
    String key;
    Context ctx;
    public addToServer(String key, FriendDatabaseObject y, Context ctx){this.y = y; this.key = key; this.ctx = ctx;}

    protected Void doInBackground(Void... x){



        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");


        client.kv("Frees", key).put(y).on(new ResponseAdapter<KvMetadata>() {
            @Override
            public void onSuccess(KvMetadata object) {
                super.onSuccess(object);
            }

            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                Toast.makeText(ctx, "Error: " + error.toString() + "(Tell Michael about this)", Toast.LENGTH_LONG);
            }
        });

        return null;
    }

}


