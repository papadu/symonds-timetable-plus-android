package com.psyngo.michael.symondstimetableplus;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvList;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;
import io.orchestrate.client.OrchestrateRequest;
import io.orchestrate.client.Result;
import io.orchestrate.client.SearchResults;

public class AddAFriend_Activity extends ActionBarActivity {

    public static List<FriendList> friends = new ArrayList<FriendList>();
    static boolean asyncRunning = false;
    static List<FriendList> NameList = new ArrayList<FriendList>();
    static nameListAdapter adapter;
    static ProgressBar pb;
    static ListView addFriendList;
    static OrchestrateRequest<KvList<FriendDatabaseObject>> listOrchestrateRequest;
    static LinearLayout errorView;
    static TextView errorTextView;
    static boolean isSearchList = false;
    static TextView noSearchTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afriend_);
        addFriendList = (ListView) findViewById(R.id.addFriendListview);
        final Context ctx = getApplicationContext();
        Typeface robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        errorView = (LinearLayout) findViewById(R.id.errorView);
        errorTextView = (TextView) findViewById(R.id.ErrorTextview);
        errorTextView.setTypeface(robotoThin);
        noSearchTv = (TextView) findViewById(R.id.noSearchTextview);
        noSearchTv.setVisibility(View.INVISIBLE);
        isSearchList = false;

        pb = (ProgressBar) findViewById(R.id.ListViewProgressBar);

        getListOfNames l = new getListOfNames(addFriendList, ctx, pb, false, "");
        l.execute();

        addFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FriendList item = NameList.get(position);
                if (!asyncRunning) {
                    addFriend n = new addFriend(ctx, item, view);
                    n.execute();
                }
            }
        });

        addFriendList.setOnScrollListener(new InfiniteScrollListener(2) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                if (!isSearchList) {
                    Log.e("myapp", "executing !searchList");
                    getListOfNames l = new getListOfNames(addFriendList, ctx, pb, true, "");
                    l.execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_afriend_, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.friend_search).getActionView();
        if (search.equals(null)) {
            Log.e("myapp", "null");
        } else {

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    AddAFriend_Activity.noSearchTv.setVisibility(View.INVISIBLE);
                    isSearchList = true;
                    getListOfNames l = new getListOfNames(addFriendList, getApplicationContext(), pb, false, query);
                    l.execute();
                    return false;
                }
            });
        }

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

    public void retry(View view) {
        getListOfNames l = new getListOfNames(addFriendList, view.getRootView().getContext(), pb, false, "");
        l.execute();
    }
}

class getListOfNames extends AsyncTask<Void, Void, ArrayList<FriendList>> {
    ListView lv;
    Context ctx;
    ProgressBar pb;
    boolean pinged = false;
    boolean success = false;
    boolean next = false;
    boolean nosearchresults = false;
    int responseCode = 0;
    String query;

    public getListOfNames(ListView lv, Context ctx, ProgressBar pb, boolean next, String query) {
        this.lv = lv;
        this.ctx = ctx;
        this.pb = pb;
        this.next = next;
        this.query = query;
    }

    protected void onPreExecute() {
        if (!next) {
            pb.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            AddAFriend_Activity.errorView.setVisibility(View.GONE);
        }
    }

    protected ArrayList<FriendList> doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            pinged = false;
            return null;
        } else {
            pinged = true;
        }

        HttpClient httpclient = new DefaultHttpClient();

        HttpGet request;


        if (LoginScreen.offlinemode) {
            //TODO: Define this block somewhere else (Copied & pasted from getFriendsList, too lazy to do it properly).
            try {
                HttpGet getrequest = new HttpGet("http://mooshoon.pythonanywhere.com/users/" + LoginScreen.username + "/friends/");
                HttpResponse timetableResponse = httpclient.execute(getrequest);
                responseCode = timetableResponse.getStatusLine().getStatusCode();

                assert (responseCode == 200);

                InputStream inputStream = timetableResponse.getEntity().getContent();


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                String result = "";
                while ((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();

                Log.e("Json", result);

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                OrchestrateResponseObject orchestrateResponse = gson.fromJson(result, OrchestrateResponseObject.class);

                for (OrchestrateResult friend : orchestrateResponse.results) {
                    FriendObjectConverter converter = new FriendObjectConverter();
                    FriendDatabaseObject v = converter.convertToFriendDatabaseObject(friend.value);
                    AddAFriend_Activity.friends.add(new FriendList(friend.path.key, v));
                }

                LoginScreen.offlinemode = false;
                success = true;
            } catch (IOException e) {
                Log.e("myapp", e.toString());
                success = false;
                return null;
            }


        }


        boolean isSearch;

        try {
            //if a list request
            if (query.equals("")) {
                isSearch = false;
                //if it's not the first page
                if (next) {
                    request = new HttpGet("http://mooshoon.pythonanywhere.com/users/?startkey=" + AddAFriend_Activity.NameList.get(AddAFriend_Activity.NameList.size() - 1).getKey());
                } else {
                    request = new HttpGet("http://mooshoon.pythonanywhere.com/users/");
                }
                //else it's a search request
            } else {
                request = new HttpGet("http://mooshoon.pythonanywhere.com/users/?query=value.name:" + query + "*");
                AddAFriend_Activity.NameList = new ArrayList<FriendList>();
                isSearch = true;
            }


            HttpResponse timetableResponse = httpclient.execute(request);
            responseCode = timetableResponse.getStatusLine().getStatusCode();

            assert responseCode == 200;

            InputStream inputStream = timetableResponse.getEntity().getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            OrchestrateResponseObject orchestrateResponse = gson.fromJson(result, OrchestrateResponseObject.class);

            if (orchestrateResponse.count == 0 && isSearch) {
                nosearchresults = true;
            }


            for (OrchestrateResult friend : orchestrateResponse.results) {
                FriendObjectConverter converter = new FriendObjectConverter();
                FriendDatabaseObject v = converter.convertToFriendDatabaseObject(friend.value);
                AddAFriend_Activity.NameList.add(new FriendList(friend.path.key, v));
            }
            success = true;
        } catch (Throwable e) {
            e.printStackTrace();
            success = false;
        }


        return null;
    }

    protected void onPostExecute(ArrayList<FriendList> arg) {
        if (pinged) {
            if (success) {

                if (!next) {
                    AddAFriend_Activity.adapter = new nameListAdapter(ctx, R.layout.simple, AddAFriend_Activity.NameList);
                    AddAFriend_Activity.addFriendList.setAdapter(AddAFriend_Activity.adapter);
                }

                if (nosearchresults) {
                    AddAFriend_Activity.noSearchTv.setText("Nope, no-one here by the name of \"" + query + "\"");
                    AddAFriend_Activity.noSearchTv.setVisibility(View.VISIBLE);
                }

                AddAFriend_Activity.adapter.notifyDataSetChanged();

                lv.setVisibility(View.VISIBLE);
                AddAFriend_Activity.errorView.setVisibility(View.GONE);
            } else {
                AddAFriend_Activity.errorView.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                AddAFriend_Activity.errorTextView.setText("Something went wrong.");
            }
        } else {
            AddAFriend_Activity.errorView.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            AddAFriend_Activity.errorTextView.setText("No Internet Connection.");
        }
        pb.setVisibility(View.INVISIBLE);
    }
}

class nameListAdapter extends ArrayAdapter<FriendList> {
    List<FriendList> objects;
    Context context;
    LayoutInflater mInflater;

    public nameListAdapter(Context context, int resource, List<FriendList> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.simple, parent, false);
        }
        String key = objects.get(position).getKey();
        String name = objects.get(position).getValue().getName();
        String date = objects.get(position).getValue().getDate();
        TextView tv = (TextView) itemView.findViewById(R.id.nametextView);
        TextView dtv = (TextView) itemView.findViewById(R.id.dateTextview);
        tv.setText(WordUtils.capitalizeFully(name));
        ImageView i = (ImageView) itemView.findViewById(R.id.imageView);
        dtv.setVisibility(View.GONE);

        itemView.setTag(0);
        i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_add_person));

        for (FriendList x : AddAFriend_Activity.friends) {
            if (x.getKey().equals(key)) {
                i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_done));
                itemView.setTag(1);
                dtv.setVisibility(View.VISIBLE);
                if (date.equals(LoginScreen.date)) {
                    dtv.setText("Up to date.");
                } else {
                    dtv.setText("Last Updated - " + date.substring(0, date.length() - 5) + ".");
                }
                break;
            }
        }

        return itemView;
    }
}

class addFriend extends AsyncTask<Void, Void, Void> {

    Context ctx;
    FriendList friend;
    View view;
    ImageView i;
    ProgressBar p;
    TextView dtv;
    boolean pinged = true;
    boolean success = true;

    int statusCode = 0;

    public addFriend(Context ctx, FriendList friend, View view) {
        this.view = view;
        this.ctx = ctx;
        this.friend = friend;
        i = (ImageView) view.findViewById(R.id.imageView);
        p = (ProgressBar) view.findViewById(R.id.LoginprogressBar);
        dtv = (TextView) view.findViewById(R.id.dateTextview);
    }

    protected void onPreExecute() {
        i.setVisibility(View.INVISIBLE);
        p.setVisibility(View.VISIBLE);
        AddAFriend_Activity.asyncRunning = true;
    }

    protected Void doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            pinged = false;
            return null;
        } else {
            pinged = true;
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://mooshoon.pythonanywhere.com/users/" + LoginScreen.username + "/friends/");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("friendkey", friend.getKey()));
        HttpResponse response = null;

        if (view.getTag().equals(0)) {
            try {

                nameValuePairs.add(new BasicNameValuePair("type", "add"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);
                statusCode = response.getStatusLine().getStatusCode();

                assert (statusCode == 200);

                AddAFriend_Activity.friends.add(friend);
                view.setTag(1);
                i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_done));

                success = true;

            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }
        } else {
            try {
                nameValuePairs.add(new BasicNameValuePair("type", "delete"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);

                statusCode = response.getStatusLine().getStatusCode();

                assert (statusCode == 200);

                success = true;
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }

            i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_add_person));
            view.setTag(0);
            int index = -1;
            for (int i = 0; i < AddAFriend_Activity.friends.size(); i++) {
                if (AddAFriend_Activity.friends.get(i).getKey().equals(friend.getKey())) {
                    index = i;
                    AddAFriend_Activity.friends.remove(index);
                }
            }
        }

        return null;
    }

    protected void onPostExecute(Void a) {
        if (!pinged) {
            Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        if (success) {
            if (view.getTag().equals(0)) {
                dtv.setVisibility(View.GONE);
            } else {
                String date = friend.getValue().getDate();
                dtv.setVisibility(View.VISIBLE);
                if (date.equals(LoginScreen.date)) {
                    dtv.setText("Up to date.");
                } else {
                    dtv.setText("Last Updated - " + date.substring(0, date.length() - 5) + ".");
                }
            }
        } else {
            Toast.makeText(ctx, "Error " + statusCode, Toast.LENGTH_SHORT).show();
        }
        i.setVisibility(View.VISIBLE);
        p.setVisibility(View.INVISIBLE);
        AddAFriend_Activity.asyncRunning = false;
    }
}


