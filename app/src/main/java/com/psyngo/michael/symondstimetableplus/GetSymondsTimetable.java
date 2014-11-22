package com.psyngo.michael.symondstimetableplus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetSymondsTimetable extends AsyncTask<String, Void, String> {

    private Context context;
    private View rootview;

    public GetSymondsTimetable(Context context, View rootview) {
        this.context = context;
        this.rootview = rootview;
    }

    String timetableHTMLstring = "";
    int responseCode;
    String title;
    RelativeLayout loadingLayout;
    TextView loadingText;
    LinearLayout loginLinear;
    Activity a;
    String username;
    String password;
    DataHandler handler;

    protected void onPreExecute() {
        loadingLayout = (RelativeLayout) rootview.findViewById(R.id.LoadingRelLayout);
        loadingText = (TextView) rootview.findViewById(R.id.LoadingtextView);
        loginLinear = (LinearLayout) rootview.findViewById(R.id.loginLinearLayout);
        a = (Activity) rootview.getContext();
        loginLinear.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText.setText("Logging in...");
    }

    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://intranet.psc.ac.uk/login.php");

        try {
            // Create HTTP POST request to Symonds Login page
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ProcessLoginForm", params[0]));
            nameValuePairs.add(new BasicNameValuePair("username", params[1]));
            nameValuePairs.add(new BasicNameValuePair("password", params[2]));
            nameValuePairs.add(new BasicNameValuePair("signin", params[3]));

            username = params[1];
            password = params[2];

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingText.setText("Getting Timetable...");
                }
            });

            //Execute HTTP Get Request for timetable page
            HttpGet request = new HttpGet("https://intranet.psc.ac.uk/records/student/timetable.php");
            HttpResponse timetableResponse = httpclient.execute(request);
            responseCode = timetableResponse.getStatusLine().getStatusCode();

            timetableHTMLstring = EntityUtils.toString(timetableResponse.getEntity());
            Document doc = Jsoup.parse(timetableHTMLstring);
            title = doc.select("title").text();

            Calendar today = Calendar.getInstance();
            int day = today.get(Calendar.DAY_OF_WEEK);
            //Get next week's Timetable
            if(day == Calendar.SATURDAY || day == Calendar.SUNDAY){
                Elements controls = doc.select("#TimetableTitle");
                Elements forward = controls.select(".forward");
                String forwardURL = forward.attr("href");
                Log.e("myapp", forwardURL.split("#")[0]);
                String r = "https://intranet.psc.ac.uk" + forwardURL.split("#")[0];
                HttpGet nextReq = new HttpGet(r);
                HttpResponse nextResponse = httpclient.execute(nextReq);
                String newHTMLString = EntityUtils.toString(nextResponse.getEntity());
                return newHTMLString;
            }

        } catch (ClientProtocolException e) {
            Log.e("myapp", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("myapp", e.toString());
            return null;
        }

        return timetableHTMLstring;
    }

    protected void onPostExecute(String arg) {
        loadingLayout.setVisibility(View.INVISIBLE);
        if (responseCode == 200) {
            if (title.equals("Student Intranet | Sign In")) {
                Toast.makeText(context, "Username or password incorrect.", Toast.LENGTH_LONG).show();
                loginLinear.setVisibility(View.VISIBLE);
            } else {
                handler = new DataHandler(context);
                handler.open();
                String query = "SELECT * FROM atable WHERE username = '" + username + "'";
                Cursor data = handler.db.rawQuery(query, null);
                if (data.moveToFirst()) {
                    ContentValues content = new ContentValues();
                    content.put("username", username);
                    content.put("password", password);
                    content.put("html", arg);
                    content.put("date", Timetable.getWeekDate(Jsoup.parse(arg)));
                    content.put("uptodate", 1);
                    handler.db.update("atable", content, "username='" + username + "'", null);
                } else {
                    long id = handler.insertData(username, password, arg, Timetable.getWeekDate(Jsoup.parse(arg)), 1);
                }

                handler.close();

                Intent intent = new Intent(context, Timetable.class);
                intent.putExtra("timetableHTML", arg);
                context.startActivity(intent);
            }
        } else if (responseCode == 0) {
            Toast.makeText(context, "Not connected to the Internet", Toast.LENGTH_LONG).show();
            loginLinear.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(context, "Error: " + responseCode, Toast.LENGTH_LONG).show();
            loginLinear.setVisibility(View.VISIBLE);
        }
    }
}
