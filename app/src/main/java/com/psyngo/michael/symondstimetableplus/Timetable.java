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


    TextView _tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Bundle extras = getIntent().getExtras();
        String myExtra = extras.getString("timetableHTML");
        parseHTML("\n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                " <head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
                "  <script type=\"text/javascript\" src=\"../../scripts/jquery/jquery-1.9.1.min.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"../../scripts/jquery/jquery-ui-1.10.2.datepicker.min.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"../../scripts/core.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"../../scripts/options.js\"></script>\n" +
                "  <title>Student Progress | Student Details</title>\n" +
                "  <link href = \"../../css/default/basic.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/default/form.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/default/records/options.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/default/records/student.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/default/records/timetable.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/print/basic.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/print/form.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/print/records/options.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/print/records/student.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/print/records/timetable.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/default/custom.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/default/jquery/smoothness/jquery-ui-1.10.2.custom.min.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                "  <link href = \"../../css/print/timetable/timetable.css\" rel = \"stylesheet\" type = \"text/css\" media = \"print\" />\n" +
                "  <link href = \"../../css/default/timetable/timetable.css\" rel = \"stylesheet\" type = \"text/css\" media = \"screen\" />\n" +
                " </head>\n" +
                " <body>\n" +
                "  <div id = \"container\">\n" +
                "   <div id = \"header\">\n" +
                "       <a href = \"../../home.php\"><img id = \"intranet\" src = \"../../images/intranet.gif\" alt = \"\" /></a>\n" +
                "       <a href = \"../home.php\"><img id = \"site\" src = \"../images/title.gif\" alt = \"\" /></a>\n" +
                "      <div id = \"top-menu\">\n" +
                "       <a href = \"#\" class = \"show-nav\">Quick Navigation</a><span>|</span>\n" +
                "       <a href = \"../contacts/contacts.php\">Contact Us</a>\n" +
                "      </div>\n" +
                "      </div>\n" +
                "      <div id = \"qnav\">\n" +
                "      <div id = \"nav-body\">\n" +
                "     <h1>Quick Navigation</h1>\n" +
                "      <table summary = \"Quick Navigation\">\n" +
                "      <tr>\n" +
                "      <td>\n" +
                "       <h4>My Subjects</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../../gs\">A2 General Studies</a></li>\n" +
                "       <li><a href = \"../../biology\">AS/A Biology</a></li>\n" +
                "       <li><a href = \"../../chemistry\">AS/A2 Chemistry</a></li>\n" +
                "       <li><a href = \"../../media\">AS/A2 Media Studies</a></li>\n" +
                "       <li><a href = \"../../psychology\">AS/A2 Psychology</a></li>\n" +
                "       <li><a href = \"../../subjects\">All Subject Intranets</a></li>\n" +
                "      </ul>\n" +
                "      </td>\n" +
                "      <td>\n" +
                "       <h4>Student Support</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../../records\">My Details</a></li>\n" +
                "       <li><a href = \"../../careers\">Careers</a></li>\n" +
                "       <li><a href = \"../../exams\">Examinations</a></li>\n" +
                "       <li><a href = \"../../itservices\">IT Services</a></li>\n" +
                "       <li><a href = \"../../studentservices\">Student Services</a></li>\n" +
                "       <li><a href = \"../../studysupport\">Study Support</a></li>\n" +
                "      </ul>\n" +
                "      </td>\n" +
                "      </tr>\n" +
                "      </table>\n" +
                "      </div>\n" +
                "       <a href = \"#\" class = \"show-nav\" id = \"hide\"><img src = \"../../images/options/exit.gif\" alt = \"\" /></a>\n" +
                "      </div>\n" +
                "      <div id = \"main\">\n" +
                "      <div id = \"content\">\n" +
                "       <h1>KENZIE WHITEHEAD, B1J</h1>\n" +
                "      <ul id = \"navigation\">\n" +
                "        <li class = \"nav-option\"><a href = \"../home.php\">Home</a></li>\n" +
                "        <li>Student Timetable</li>\n" +
                "      </ul>\n" +
                "      <div id = \"menu-options\">\n" +
                "      <div>\n" +
                "      <h1>Student Details</h1>\n" +
                "      <table summary = \"Class Options\">\n" +
                "      <tr>\n" +
                "      <td>\n" +
                "      <h4>Basic Information</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../home.php\">Student Details</a></li>\n" +
                "       <li><a href = \"../student/timetable.php\">Timetable</a></li>\n" +
                "       <li><a href = \"../student/mobile.php\">Mobile Phone Settings</a></li>\n" +
                "      </ul>\n" +
                "      <h4>Examinations &amp; Qualifications</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../student/qoe.php\">Qualifications on Entry</a></li>\n" +
                "       <li><a href = \"../student/qualifications.php\">College Qualifications</a></li>\n" +
                "       <li class = \"unavailable\">Module Results</li>\n" +
                "       <li><a href = \"../student/exams.php\">Exam Timetable</a></li>\n" +
                "      </ul>\n" +
                "      </td>\n" +
                "      <td>\n" +
                "      <h4>Attendance</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../student/attendance/marks.php\">Attendance Marks</a></li>\n" +
                "       <li><a href = \"../student/attendance/summary.php\">Summary</a></li>\n" +
                "      </ul>\n" +
                "      <h4>Coursework Deadlines</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../student/coursework.php\">View Deadlines</a></li>\n" +
                "      </ul>\n" +
                "      <h4>Student Support</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../student/support/access.php\">Exam Arrangements</a></li>\n" +
                "       <li><a href = \"../student/timetableplanner.php\">Timetable Planner</a></li>\n" +
                "      </ul>\n" +
                "      </td>\n" +
                "      <td>\n" +
                "      <h4>Progress Reviews</h4>\n" +
                "      <ul>\n" +
                "       <li><a href = \"../student/progress/review.php?Student=38000550686742\">Progress Summary</a>\n" +
                "      </li>\n" +
                "       <li class = \"unavailable\">Boarding Review</li>\n" +
                "       <li class = \"unavailable\">My A2 Choices</li>\n" +
                "      </ul>\n" +
                "      </td>\n" +
                "      </tr>\n" +
                "      </table>\n" +
                "      </div>\n" +
                "      <a href = \"#\" class = \"options\" id = \"exit\"><img src = \"../../images/options/exit.gif\" alt = \"Close menu\" /></a>\n" +
                "      </div>\n" +
                "      <div id = \"student-overview\"><a href = \"#\" class = \"options\">Student Options</a></div>\n" +
                "      <div id = \"report-wide\">\n" +
                "      <div id='TimetableTitle'>\n" +
                "       <a name='timetablecontrols'></a><div id='TimetableControls'><div><a href='/records/student/timetable.php?v=list&w=616#timetablecontrols' class='listview' title='View as a list'></a><a href='#' onclick='javascript:return false;' class='disabled gridview' title='View as a grid (selected)'></a></div><div><input type='hidden' id='DatePicker'/></div><div><a class='back' href='/records/student/timetable.php?w=615#timetablecontrols' title='Go back 1 week'></a><a class='forward' href='/records/student/timetable.php?w=617#timetablecontrols' title='Go forward 1 week'></a></div></div>\n" +
                "       <p class='print'>Generated 5:31pm 30 October 2014</p>\n" +
                "       <h3><span class='print'>Timetable for Kenzie Whitehead /</span>20 to 26 October 2014</h3>\n" +
                "       <a name='TimetableTop'></a>\n" +
                "      </div>\n" +
                "      <table id='Timetable' class='reduced'>\n" +
                "       <tr>\n" +
                "       <th class='time'>08:30</th>\n" +
                "       <th class='day'><a href='/records/student/timetable.php?w=615&d=last#TimetableTop' class='back'></a><span class='normal'>Monday 20</span><span class='full'>Mon 20 Oct</span><a href='/records/student/timetable.php?w=616&d=2#TimetableTop' class='forward'></a></th>\n" +
                "       <th class='day'><a href='/records/student/timetable.php?w=616&d=1#TimetableTop' class='back'></a><span class='normal'>Tuesday 21</span><span class='full'>Tue 21 Oct</span><a href='/records/student/timetable.php?w=616&d=3#TimetableTop' class='forward'></a></th>\n" +
                "       <th class='day'><a href='/records/student/timetable.php?w=616&d=2#TimetableTop' class='back'></a><span class='normal'>Wednesday 22</span><span class='full'>Wed 22 Oct</span><a href='/records/student/timetable.php?w=616&d=4#TimetableTop' class='forward'></a></th>\n" +
                "       <th class='day current'><a href='/records/student/timetable.php?w=616&d=3#TimetableTop' class='back'></a><span class='normal'>Thursday 23</span><span class='full'>Thu 23 Oct</span><a href='/records/student/timetable.php?w=616&d=5#TimetableTop' class='forward'></a></th>\n" +
                "       <th class='day'><a href='/records/student/timetable.php?w=616&d=4#TimetableTop' class='back'></a><span class='normal'>Friday 24</span><span class='full'>Fri 24 Oct</span><a href='/records/student/timetable.php?w=617&d=1#TimetableTop' class='forward'></a></th>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>09:25</th>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Media Studies</p>\n" +
                "         <p class='subtitle'>Katy Ellis</p>\n" +
                "         <p class='room'>CC203 <span>8:30am</span></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Psychology</p>\n" +
                "         <p class='subtitle'>Christina Rycroft</p>\n" +
                "         <p class='room'>JS306 <span>8:30am</span></p>\n" +
                "        <td rowspan='2' class='day current item lesson'>\n" +
                "         <p class='title'>Chemistry</p>\n" +
                "         <p class='subtitle'>Annett Neubauer</p>\n" +
                "         <p class='room'>SC217 <span>8:30am</span></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Symonds Lecture Programme</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "         <p class='room'>SC101 <span>8:30am</span></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>10:20</th>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Human Biology</p>\n" +
                "         <p class='subtitle'>Penny Pugh</p>\n" +
                "         <p class='room'>SC109 <span>9:25am</span></p>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>10:40</th>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Break</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Break</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Break</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day current break blank'>\n" +
                "         <p class='title'>Break</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Break</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>11:35</th>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Media Studies</p>\n" +
                "         <p class='subtitle'>Katy Ellis</p>\n" +
                "         <p class='room'>CC203 <span>10:40am</span></p>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td rowspan='2' class='day item lesson'>\n" +
                "         <p class='title'>Chemistry</p>\n" +
                "         <p class='subtitle'>Annett Neubauer</p>\n" +
                "         <p class='room'>SC217 <span>10:40am</span></p>\n" +
                "        <td class='day current blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Human Biology</p>\n" +
                "         <p class='subtitle'>Penny Pugh</p>\n" +
                "         <p class='room'>SC109 <span>10:40am</span></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>12:30</th>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Chemistry</p>\n" +
                "         <p class='subtitle'>Annett Neubauer</p>\n" +
                "         <p class='room'>SC217 <span>11:35am</span></p>\n" +
                "        <td rowspan='1' class='day current item lesson'>\n" +
                "         <p class='title'>Media Studies</p>\n" +
                "         <p class='subtitle'>Amy Charlewood</p>\n" +
                "         <p class='room'>CC203 <span>11:35am</span></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Psychology</p>\n" +
                "         <p class='subtitle'>Christina Rycroft</p>\n" +
                "         <p class='room'>JS306 <span>11:35am</span></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>13:00</th>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item tutorgroup'>\n" +
                "         <p class='title'>Tutor Group</p>\n" +
                "         <p class='subtitle'>David Francis</p>\n" +
                "         <p class='room'>SC217 <span>12:30pm</span></p>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td class='day current blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item tutorgroup'>\n" +
                "         <p class='title'>Tutor Group</p>\n" +
                "         <p class='subtitle'>David Francis</p>\n" +
                "         <p class='room'>SC217 <span>12:30pm</span></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>13:50</th>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Lunch</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Workshop</p>\n" +
                "         <p class='subtitle'>Jennie Barfield</p>\n" +
                "         <p class='room'>FN121 <span>1:00pm</span></p>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Lunch</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day current break blank'>\n" +
                "         <p class='title'>Lunch</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "        <td rowspan='1' class='day break blank'>\n" +
                "         <p class='title'>Lunch</p>\n" +
                "         <p class='subtitle'></p>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>14:45</th>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Psychology</p>\n" +
                "         <p class='subtitle'>Geoffrey Rolls, Christina Rycroft</p>\n" +
                "         <p class='room'>JS306 <span>1:50pm</span></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Human Biology</p>\n" +
                "         <p class='subtitle'>Penny Pugh</p>\n" +
                "         <p class='room'>SC109 <span>1:50pm</span></p>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td class='day current blank'>Study Period</td>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "       </tr>\n" +
                "       <tr>\n" +
                "       <th class='time'>16:35</th>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Human Biology</p>\n" +
                "         <p class='subtitle'>Penny Pugh</p>\n" +
                "         <p class='room'>SC109 <span>2:45pm</span></p>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Psychology</p>\n" +
                "         <p class='subtitle'>Geoffrey Rolls</p>\n" +
                "         <p class='room'>JS306 <span>2:45pm</span></p>\n" +
                "        <td class='day blank'>Study Period</td>\n" +
                "        <td class='day current blank'>Study Period</td>\n" +
                "        <td rowspan='1' class='day item lesson'>\n" +
                "         <p class='title'>Media Studies</p>\n" +
                "         <p class='subtitle'>Amy Charlewood</p>\n" +
                "         <p class='room'>CC203 <span>2:45pm</span></p>\n" +
                "       </tr>\n" +
                "      </table>\n" +
                "       <div id='Floaters' class='lessonList'>\n" +
                "        <h4>&quot;Floating&quot; lessons</h4>\n" +
                "        <p>The following lessons are &quot;floating&quot; lessons.  This means that sessions run throughout the week and the student is expected to attend one of them.</p>\n" +
                "        <ul>\n" +
                "         <li><span class='subject'>Multigym</span><span class='teacher'>Jenny Phillips</span><span class='room'>MH203</span></li>\n" +
                "        </ul>\n" +
                "       </div>\n" +
                "<script language='javascript'>\n" +
                " $(function(){var zi=500;$.each($(\"th.time\"),function(){$(this).css(\"z-index\",zi);zi--;$(this).wrapInner($(\"<span/>\").css(\"z-index\",zi+1000));$(this).wrapInner($(\"<div/>\").css(\"z-index\",zi+100));});$(\"#DatePicker\").datepicker({showOn:\"button\",buttonImage:\"/css/images/timetable/calendar-orange.png\",buttonImageOnly:true,buttonText:\"Choose date\",dateFormat: \"yy-mm-dd\",minDate:\"2014-10-13\",maxDate:\"2014-11-30\",onSelect:function(value,date){top.location=\"/records/student/timetable.php?w=\"+value+\"\";}});});\n" +
                "</script>\n" +
                "      </div>\n" +
                "      </div>\n" +
                "<div id = \"footer\">\n" +
                "       <a href = \"mailto:webmaster@psc.ac.uk\">Contact Webmaster</a>\n" +
                "       <span>|</span><a href = \"../../appearance\">Change Appearance</a>\n" +
                "      </div>\n" +
                "      </div>\n" +
                "  </div>\n" +
                " </body>\n" +
                "</html>");

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

                lessonIndex += length;
                if(lessonIndex>9){
                lessonIndex=0;
                }

                Calendar endcal = Calendar.getInstance();
                Calendar startcal = Calendar.getInstance();

                try {
                    startcal.setTime(new SimpleDateFormat("HH:mm").parse(time));
                    endcal.setTime(new SimpleDateFormat("HH:mm").parse(times[lessonIndex + length]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Log.d("myapp", j + time + subject + length + nexttime);

                Lesson lesson = new Lesson(time, subject, teacher, room, length, nexttime, whoElseFree, startcal, endcal, subjectColor);

                Week.add(lesson);

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

            int dayNum;
            if (openCurrentDay == true) {
                Calendar calendar = Calendar.getInstance();
                dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (dayNum > 5 || dayNum == 0) {
                    dayNum = 1;
                }
                openCurrentDay = false;

            } else {
                dayNum = args.getInt(ARG_SECTION_NUMBER);
            }
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

        TextView quickviewTitle = (TextView) rootView.findViewById(R.id.subject_textview);
        TextView timelefttextview = (TextView) rootView.findViewById(R.id.time_left_texview);
        TextView prefixtextview = (TextView) rootView.findViewById(R.id.prefix_textview);
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

            quickviewTitle.setVisibility(View.VISIBLE);
            timelefttextview.setVisibility(View.VISIBLE);
            prefixtextview.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);


            for (Lesson les : todaysLessons) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.DAY_OF_MONTH, les.getEndTime().get(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.MONTH, les.getEndTime().get(Calendar.MONTH));
                cal.set(Calendar.YEAR, les.getEndTime().get(Calendar.YEAR));
                cal.add(Calendar.HOUR_OF_DAY, -10);


                quickviewTitle.setText("No more Lessons today");
                prefixtextview.setText("");
                timelefttextview.setText("");


                Date dt = cal.getTime();
                long currenttime = dt.getTime();
                if (currenttime > les.getStartTime().getTime().getTime() && currenttime < les.getEndTime().getTime().getTime()) {

                    Log.d("myapp", les.getEndTime().getTime().toString());
                    Log.d("myapp", cal.getTime().toString());

                    String timeleft = String.valueOf(Math.abs(les.getEndTime().getTimeInMillis() - cal.getTimeInMillis()) / 60000);
                    timelefttextview.setText(timeleft + " Minutes left");
                    quickviewTitle.setText(les.getLessonName());

                    prefixtextview.setText("Happening now");
                    break;

                } else if (les.getLessonName().equals("Break") != true && currenttime < les.getStartTime().getTime().getTime() && les.getLessonName().equals("Free Period") != true) {


                    quickviewTitle.setText(les.getLessonName());
                    long timeleft = Math.abs(les.getStartTime().getTimeInMillis() - cal.getTimeInMillis()) / 60000;
                    if (timeleft < 60) {
                        String timeleftminutes = String.valueOf(timeleft);
                        timelefttextview.setText("in " + timeleftminutes + " Minutes");
                    } else {
                        String timeleftminutes = String.valueOf(timeleft % 60);
                        String timelefthours = String.valueOf((timeleft - (timeleft % 60)) / 60) + " Hours";
                        timelefttextview.setText("in " + timelefthours + " and " + timeleftminutes + " Minutes");
                    }

                    prefixtextview.setText("Your next lesson is");


                    break;


                }

            }
        } else {
            quickviewTitle.setVisibility(View.GONE);
            timelefttextview.setVisibility(View.GONE);
            prefixtextview.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }


        ;


    }


}
