package com.psyngo.michael.symondstimetableplus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
    LinearLayout existingLinear;
    Activity a;
    String username;
    String password;
    DataHandler handler;
    boolean loginScreen;

    protected void onPreExecute() {
        loadingLayout = (RelativeLayout) rootview.findViewById(R.id.LoadingRelLayout);
        loadingText = (TextView) rootview.findViewById(R.id.LoadingtextView);
        loginLinear = (LinearLayout) rootview.findViewById(R.id.loginLinearLayout);
        existingLinear = (LinearLayout) rootview.findViewById(R.id.existingAccountLinLayout);
        a = (Activity) rootview.getContext();
        if(loginLinear.getVisibility()==(View.VISIBLE)){
            loginScreen = true;
        }
        else{
            loginScreen=false;
        }
        loginLinear.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        existingLinear.setVisibility(View.INVISIBLE);
        loadingText.setText("Logging in...");
    }

    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://intranet.psc.ac.uk/login.php");

        String ProcessLoginForm = "true";
        String signin = "Sign In";

        try {
            // Create HTTP POST request to Symonds Login page
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ProcessLoginForm", ProcessLoginForm));
            nameValuePairs.add(new BasicNameValuePair("username", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            nameValuePairs.add(new BasicNameValuePair("signin", signin));

            username = params[0];
            password = params[1];

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
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
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
//existing account linear set visibility
    protected void onPostExecute(String arg) {
        if (responseCode == 200) {
            if (title.equals("Student Intranet | Sign In")) {
                Toast.makeText(context, "Username or password incorrect.", Toast.LENGTH_LONG).show();
                loginLinear.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.INVISIBLE);
                existingLinear.setVisibility(View.INVISIBLE);
            } else {
                Uri contentUri = Uri.withAppendedPath(DbContentProvider.CONTENT_URI, "atable");
                LoginScreen.date = Timetable.getWeekDate(Jsoup.parse(arg));
                ContentValues content = new ContentValues();
                content.put("username", username);
                content.put("password", password);
                content.put("html", arg);
                content.put("date", LoginScreen.date);
                content.put("uptodate", 1);

                Cursor data = context.getContentResolver().query(contentUri, null, "username = '"+username+"'",null,null);

                if (data.moveToFirst()) {
                    int updateResult = context.getContentResolver().update(contentUri, content, "username='"+username+"'", null);
                } else {
                    Uri resultUri = context.getContentResolver().insert(contentUri, content);
                }

                getFriendsList l = new getFriendsList(context, null, arg);
                l.execute();
            }
        } else if (responseCode == 0) {
            Toast.makeText(context, "Not connected to the Internet", Toast.LENGTH_LONG).show();
            if(loginScreen){
                loginLinear.setVisibility(View.VISIBLE);
            }
            else{
                existingLinear.setVisibility(View.VISIBLE);
            }
            loadingLayout.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(context, "Error: " + responseCode, Toast.LENGTH_LONG).show();
            if(loginScreen){
                loginLinear.setVisibility(View.VISIBLE);
            }
            else{
                existingLinear.setVisibility(View.VISIBLE);
            }
            loadingLayout.setVisibility(View.INVISIBLE);
        }
    }
}
