package com.psyngo.michael.symondstimetableplus;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
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

import org.apache.commons.lang3.text.WordUtils;

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
                if(!isSearchList) {
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

        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");

        try {
            client.ping();
            pinged = true;
        } catch (Throwable e) {
            e.printStackTrace();
            pinged = false;
        }

        if (LoginScreen.offlinemode && pinged) {
            try {
                Iterable<KvObject<FriendDatabaseObject>> results =
                        client.relation("Frees", LoginScreen.username)
                                .limit(60)
                                .get(FriendDatabaseObject.class, "friends")
                                .get(10, TimeUnit.SECONDS);
                for (KvObject<FriendDatabaseObject> i : results) {
                    boolean alreadyFriend = false;
                    for (int x = 0; x < AddAFriend_Activity.friends.size(); x++) {
                        if (AddAFriend_Activity.friends.get(x).getKey().equals(i.getKey())) {
                            alreadyFriend = true;
                        }
                    }
                    if (!alreadyFriend) {
                        AddAFriend_Activity.friends.add(new FriendList(i.getKey(), i.getValue()));
                    }
                }
                LoginScreen.offlinemode = false;
            } catch (Throwable e) {

            }
        }

        KvList<FriendDatabaseObject> l;

        if (pinged) {
            try {
                if (query.equals("")) {
                    if (next) {
                        AddAFriend_Activity.listOrchestrateRequest = client.listCollection("Frees")
                                .startKey(AddAFriend_Activity.NameList.get(AddAFriend_Activity.NameList.size() - 1).getKey())
                                .limit(50)
                                .get(FriendDatabaseObject.class);
                    } else {

                        AddAFriend_Activity.listOrchestrateRequest = client.listCollection("Frees")
                                .limit(50)
                                .get(FriendDatabaseObject.class);
                        AddAFriend_Activity.NameList = new ArrayList<FriendList>();
                    }

                    l = AddAFriend_Activity.listOrchestrateRequest.get(20, TimeUnit.SECONDS);

                    for (KvObject<FriendDatabaseObject> o : l) {
                        AddAFriend_Activity.NameList.add(new FriendList(o.getKey(), o.getValue()));
                    }
                } else {
                    String luceneQuery = "value.name:*" + query + "*";
                    SearchResults<FriendDatabaseObject> searchResults = client.searchCollection("Frees")
                            .limit(20)
                            .get(FriendDatabaseObject.class, luceneQuery)
                            .get(15, TimeUnit.SECONDS);
                    AddAFriend_Activity.NameList = new ArrayList<FriendList>();
                    if(searchResults.getTotalCount() == 0){
                        nosearchresults = true;
                    }
                    for (Result<FriendDatabaseObject> x : searchResults) {
                        AddAFriend_Activity.NameList.add(new FriendList(x.getKvObject().getKey(), x.getKvObject().getValue()));
                    }
                }

                success = true;
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }
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

                if(nosearchresults){
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
            AddAFriend_Activity.errorTextView.setText("Can't reach server.");
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
        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");
        if (view.getTag().equals(0)) {

            try {
                client.ping();
                pinged = true;
            } catch (Throwable e) {
                e.printStackTrace();
                pinged = false;
            }

            try {
                if (pinged) {
                    boolean result = client.relation("Frees", LoginScreen.username)
                            .to("Frees", friend.getKey())
                            .put("friends")
                            .get(10, TimeUnit.SECONDS);

                    AddAFriend_Activity.friends.add(friend);
                    view.setTag(1);
                    i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_done));

                    success = true;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }
        } else {
            try {
                if (pinged) {

                    boolean result = client.relation("Frees", LoginScreen.username)
                            .to("Frees", friend.getKey())
                            .purge("friends")
                            .get(10, TimeUnit.SECONDS);
                    success = true;
                }
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
            Toast.makeText(ctx, "Cannot reach server", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ctx, "Operation timed out, try again.", Toast.LENGTH_SHORT).show();
        }
        i.setVisibility(View.VISIBLE);
        p.setVisibility(View.INVISIBLE);
        AddAFriend_Activity.asyncRunning = false;
    }
}


