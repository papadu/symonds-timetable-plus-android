package com.psyngo.michael.symondstimetableplus;

import android.os.AsyncTask;
import android.util.Log;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class symondspost extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://intranet.psc.ac.uk/login.php");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ProcessLoginForm", params[0]));
            nameValuePairs.add(new BasicNameValuePair("username", params[1]));
            nameValuePairs.add(new BasicNameValuePair("password", params[2]));
            nameValuePairs.add(new BasicNameValuePair("signin", params[3]));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);


            HttpGet request = new HttpGet("https://intranet.psc.ac.uk/records/student/timetable.php");
            HttpResponse secondResponse = httpclient.execute(request);

            Log.d("myapp", "response " + EntityUtils.toString(secondResponse.getEntity()));

        } catch (ClientProtocolException e) {
            Log.e("myapp", e.toString());
        } catch (IOException e) {
            Log.e("myapp", e.toString());
        }
        return null;
    }


}
