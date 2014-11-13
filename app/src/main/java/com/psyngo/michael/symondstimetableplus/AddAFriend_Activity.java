package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvList;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;
import io.orchestrate.client.ResponseAdapter;


public class AddAFriend_Activity extends ActionBarActivity {


    public static List<FriendList> friends = new ArrayList<FriendList>();
    static List<String> friendkeys = new ArrayList<String>();
    static boolean asyncRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afriend_);
        ListView addFriendList = (ListView) findViewById(R.id.addFriendListview);
        final Context ctx = getApplicationContext();

        ProgressBar pb = (ProgressBar) findViewById(R.id.ListViewProgressBar);

        getListOfNames l = new getListOfNames(addFriendList, ctx, pb);
        l.execute();


        addFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedKey;
                TextView clicked = (TextView) view.findViewById(R.id.nametextView);
                clickedKey = clicked.getText().toString().toUpperCase();
                if(!asyncRunning) {
                    getName n = new getName(ctx, clickedKey, view);
                    n.execute();
                }




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

class getListOfNames extends AsyncTask<Void, Void, ArrayList<String>>{
    ListView lv;
    Context ctx;
    ProgressBar pb;
    boolean pinged = false;
    boolean success = true;
    public getListOfNames(ListView lv, Context ctx, ProgressBar pb){
        this.lv = lv;
        this.ctx = ctx;
        this.pb = pb;
    }

    protected void onPreExecute(){
        pb.setVisibility(View.VISIBLE);
        lv.setVisibility(View.INVISIBLE);


    }

    protected ArrayList<String> doInBackground(Void... params){


        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");

        try {
            client.ping();
            pinged = true;
        } catch (Throwable e) {
            e.printStackTrace();
            pinged = false;


        }
        ArrayList<String> names = new ArrayList<String>();
        if(pinged) {
            try {
                KvList<FriendDatabaseObject> results =
                        client.listCollection("Frees")
                                .limit(40)
                                .withValues(Boolean.FALSE)
                                .get(FriendDatabaseObject.class)
                                .get(20, TimeUnit.SECONDS);


                for (KvObject<FriendDatabaseObject> kvObject : results) {
                    // do something with the object

                    names.add(kvObject.getKey());
                }
                success = true;
            } catch (Throwable e) {
                e.printStackTrace();
                success = false;
            }
        }

        return names;
    }

    protected void onPostExecute(ArrayList<String> arg){
        if(!pinged){
            Toast.makeText(ctx, "Cannot reach server", Toast.LENGTH_LONG).show();


        }
        if(success) {
            nameListAdapter adapter = new nameListAdapter(ctx, R.layout.simple, arg);
            for (FriendList fl : AddAFriend_Activity.friends) {
                AddAFriend_Activity.friendkeys.add(fl.getKey());
            }
            lv.setAdapter(adapter);

            lv.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(ctx, "Operation Timed out, try again.", Toast.LENGTH_LONG).show();
        }
        pb.setVisibility(View.INVISIBLE);

    }
}

class nameListAdapter extends ArrayAdapter<String> {
    List<String> objects;
    Context context;
    LayoutInflater mInflater;

    public nameListAdapter(Context context, int resource, List<String> objects) {
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

        String name = objects.get(position);
        TextView tv = (TextView) itemView.findViewById(R.id.nametextView);
        tv.setText(name.substring(0,1) + name.split(" ")[0].substring(1).toLowerCase() + " " + name.split(" ")[1].substring(0,1) + name.split(" ")[1].substring(1).toLowerCase());
        ImageView i = (ImageView) itemView.findViewById(R.id.imageView);

        if(AddAFriend_Activity.friendkeys.contains(name)){
            i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_done));
            itemView.setTag(1);
        }
        else{
            itemView.setTag(0);
            i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_add_person));
        }



        return itemView;

    }
}

class getName extends AsyncTask<Void, Void, Void>{

    Context ctx;
    String key;
    View view;
    ImageView i;
    ProgressBar p;
    boolean pinged = false;
    boolean success = true;

    public getName(Context ctx, String key, View view){
        this.view = view;
        this.ctx = ctx;
        this.key = key;
        i = (ImageView) view.findViewById(R.id.imageView);
        p = (ProgressBar) view.findViewById(R.id.LoginprogressBar);
    }

    protected void onPreExecute(){
        i.setVisibility(View.INVISIBLE);
        p.setVisibility(View.VISIBLE);
        AddAFriend_Activity.asyncRunning = true;
    }



    protected Void doInBackground(Void... params){



        if(view.getTag().equals(0)){


            Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");


            try {
                client.ping();
                pinged = true;
            } catch (Throwable e) {
                e.printStackTrace();
                pinged = false;


            }

            try {
                if (pinged) {
                    client.kv("Frees", key)
                            .get(FriendDatabaseObject.class)
                            .on(new ResponseAdapter<KvObject<FriendDatabaseObject>>() {
                                @Override
                                public void onFailure(final Throwable error) {
                                    Toast.makeText(ctx, "Error: " + error.toString() + " (Tell Michael about this)", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccess(final KvObject<FriendDatabaseObject> object) {
                                    if (object == null) {
                                        Toast.makeText(ctx, "Error: " + "404", Toast.LENGTH_LONG);
                                    }


                                    FriendDatabaseObject data = object.getValue();
                                    AddAFriend_Activity.friends.add(new FriendList(key, data));
                                    AddAFriend_Activity.friendkeys.add(key);
                                    view.setTag(1);
                                    i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_done));
                                    Log.d("myapp", "sucess");
                                    success = true;
                                }
                            }).get(10, TimeUnit.SECONDS);
                }
            }
            catch (Throwable e){
                e.printStackTrace();
                success = false;
            }




        }
        else{
            i.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_add_person));
            view.setTag(0);

            for(Iterator<FriendList> fl = AddAFriend_Activity.friends.iterator(); fl.hasNext();){
                FriendList f = fl.next();
                if(f.getKey().equals(key)){
                    fl.remove();
                    AddAFriend_Activity.friendkeys.remove(key);
                }
            }

        }



        return null;

    }

    protected void onPostExecute(Void a){
        if(!pinged){
            Toast.makeText(ctx, "Cannot reach server", Toast.LENGTH_SHORT).show();
        }
        if(!success){
            Toast.makeText(ctx, "Operation timed out, try again.", Toast.LENGTH_SHORT).show();
        }
        i.setVisibility(View.VISIBLE);
        p.setVisibility(View.INVISIBLE);
        AddAFriend_Activity.asyncRunning = false;
    }
}


