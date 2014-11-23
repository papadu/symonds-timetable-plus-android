package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class AddAFriend_Activity extends ActionBarActivity {

    public static List<FriendList> friends = new ArrayList<FriendList>();
    static boolean asyncRunning = false;
    static List<FriendList> NameList = new ArrayList<FriendList>();
    static nameListAdapter adapter;
    ProgressBar pb;
    static ListView addFriendList;
    static OrchestrateRequest<KvList<FriendDatabaseObject>> listOrchestrateRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afriend_);
        addFriendList = (ListView) findViewById(R.id.addFriendListview);
        final Context ctx = getApplicationContext();

        pb = (ProgressBar) findViewById(R.id.ListViewProgressBar);

        getListOfNames l = new getListOfNames(addFriendList, ctx, pb, false);
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

                getListOfNames l = new getListOfNames(addFriendList, ctx, pb, true);
                l.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_afriend_, menu);

        return true;
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
}

class getListOfNames extends AsyncTask<Void, Void, ArrayList<FriendList>> {
    ListView lv;
    Context ctx;
    ProgressBar pb;
    boolean pinged = false;
    boolean success = true;
    boolean next = false;

    public getListOfNames(ListView lv, Context ctx, ProgressBar pb, boolean next) {
        this.lv = lv;
        this.ctx = ctx;
        this.pb = pb;
        this.next = next;
    }

    protected void onPreExecute() {
        if (!next) {
            pb.setVisibility(View.VISIBLE);
            lv.setVisibility(View.INVISIBLE);
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

        if(LoginScreen.offlinemode && pinged){
            try {
                Iterable<KvObject<FriendDatabaseObject>> results =
                        client.relation("Frees", LoginScreen.username)
                                .get(FriendDatabaseObject.class, "friends")
                                .get(10, TimeUnit.SECONDS);
                for (KvObject<FriendDatabaseObject> i : results) {
                    AddAFriend_Activity.friends.add(new FriendList(i.getKey(), i.getValue()));
                }
                LoginScreen.offlinemode = false;
            } catch (Throwable e){

            }
        }

        KvList<FriendDatabaseObject> l;

        if (pinged) {
            try {

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

                success = true;
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }
        }

        return null;
    }

    protected void onPostExecute(ArrayList<FriendList> arg) {
        if (!pinged) {
            Toast.makeText(ctx, "Cannot reach server", Toast.LENGTH_LONG).show();
        }
        if (success) {

            if (!next) {
                AddAFriend_Activity.adapter = new nameListAdapter(ctx, R.layout.simple, AddAFriend_Activity.NameList);
                AddAFriend_Activity.addFriendList.setAdapter(AddAFriend_Activity.adapter);
            }

            AddAFriend_Activity.adapter.notifyDataSetChanged();

            lv.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(ctx, "Operation Timed out, try again.", Toast.LENGTH_LONG).show();
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
        TextView tv = (TextView) itemView.findViewById(R.id.nametextView);
        tv.setText(WordUtils.capitalizeFully(name));
        ImageView i = (ImageView) itemView.findViewById(R.id.imageView);

        itemView.setTag(0);
        i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_add_person));

        for (FriendList x : AddAFriend_Activity.friends) {
            if (x.getKey().equals(key)) {
                i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_done));
                itemView.setTag(1);
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
    boolean pinged = true;
    boolean success = true;

    public addFriend(Context ctx, FriendList friend, View view) {
        this.view = view;
        this.ctx = ctx;
        this.friend = friend;
        i = (ImageView) view.findViewById(R.id.imageView);
        p = (ProgressBar) view.findViewById(R.id.LoginprogressBar);
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
                }
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }

            i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_add_person));
            view.setTag(0);

            AddAFriend_Activity.friends.remove(friend);
        }

        return null;
    }

    protected void onPostExecute(Void a) {
        if (!pinged) {
            Toast.makeText(ctx, "Cannot reach server", Toast.LENGTH_SHORT).show();
        }
        if (!success) {
            Toast.makeText(ctx, "Operation timed out, try again.", Toast.LENGTH_SHORT).show();
        }
        i.setVisibility(View.VISIBLE);
        p.setVisibility(View.INVISIBLE);
        AddAFriend_Activity.asyncRunning = false;
    }
}


