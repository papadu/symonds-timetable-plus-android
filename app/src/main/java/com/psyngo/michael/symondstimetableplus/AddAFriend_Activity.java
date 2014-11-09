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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvList;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;


public class AddAFriend_Activity extends ActionBarActivity {


    public static List<FriendList> friends = new ArrayList<FriendList>();
    static List<String> friendkeys = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afriend_);
        ListView addFriendList = (ListView) findViewById(R.id.addFriendListview);
        final Context ctx = getApplicationContext();
        getListOfNames l = new getListOfNames(addFriendList, ctx);
        l.execute();


        addFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedKey;
                TextView clicked = (TextView) view.findViewById(R.id.nametextView);
                clickedKey = clicked.getText().toString().toUpperCase();
                Log.d("myapp", clickedKey);
                ImageView i = (ImageView) view.findViewById(R.id.imageView);
                if(view.getTag().equals(0)){
                    i.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_done));
                    view.setTag(1);
                    getName n = new getName(ctx, clickedKey);
                    n.execute();



                }
                else{
                    i.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_add_person));
                    view.setTag(0);
                    for(FriendList fl : friends){
                        if(fl.getKey().equals(clickedKey)){
                            friends.remove(fl);
                            friendkeys.remove(clickedKey);

                        }
                    }
                    Log.d("myapp", friends.toString());
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
    public getListOfNames(ListView lv, Context ctx){
        this.lv = lv;
        this.ctx = ctx;
    }

    protected ArrayList<String> doInBackground(Void... params){


        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");
        KvList<FriendDatabaseObject> results =
                client.listCollection("Frees")
                        .withValues(Boolean.FALSE)
                        .get(FriendDatabaseObject.class)
                        .get();
        ArrayList<String> names = new ArrayList<String>();
        for (KvObject<FriendDatabaseObject> kvObject : results) {
            // do something with the object

            names.add(kvObject.getKey());
        }

        return names;
    }

    protected void onPostExecute(ArrayList<String> arg){
        nameListAdapter adapter = new nameListAdapter(ctx, R.layout.simple, arg);
        for(FriendList fl : AddAFriend_Activity.friends){
            AddAFriend_Activity.friendkeys.add(fl.getKey());
        }
        lv.setAdapter(adapter);

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

class getName extends AsyncTask<Void, Void, FriendDatabaseObject>{

    Context ctx;
    String key;
    public getName(Context ctx, String key){

        this.ctx = ctx;
        this.key = key;
    }

    protected FriendDatabaseObject doInBackground(Void... params){


        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");
        KvObject<FriendDatabaseObject> object =
                client.kv("Frees", key)
                        .get(FriendDatabaseObject.class)
                        .get();
        if (object == null) {
            System.out.println("'someKey' does not exist.");
        } else {
            FriendDatabaseObject data = object.getValue();
            return data;
        }
        return null;

    }

    protected void onPostExecute(FriendDatabaseObject arg){
       AddAFriend_Activity.friends.add(new FriendList(key, arg));
        Log.d("myapp", AddAFriend_Activity.friends.toString());
    }
}


