package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;

public class LoginScreen extends ActionBarActivity {

    static LinearLayout newAc;
    static LinearLayout existingAc;
    static RelativeLayout loading;
    DataHandler db;
    static public int viewstate = 0;
    static List<String[]> accs = new ArrayList<String[]>();
    static String username;
    static boolean offlinemode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        String user;
        String pass;
        String html;
        String date;
        int uptodate;
        db = new DataHandler(getBaseContext());
        db.open();
        Cursor C = db.returnData();
        if (C.moveToFirst()) {
            viewstate = 1;
            do {
                user = C.getString(0);
                pass = C.getString(1);
                html = C.getString(2);
                date = C.getString(3);
                uptodate = C.getInt(4);
                accs.add(new String[]{user, pass, html, date, String.valueOf(uptodate)});
            }
            while (C.moveToNext());
        }
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_login_screen, container, false);
            Typeface robotoLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            EditText usernameEdit = (EditText) rootView.findViewById(R.id.username);
            EditText passwordEdit = (EditText) rootView.findViewById(R.id.password);
            TextView subtitle = (TextView) rootView.findViewById(R.id.login_prompt);
            ListView accountsList = (ListView) rootView.findViewById(R.id.accountslistView);
            subtitle.setTypeface(robotoLight);
            usernameEdit.setTypeface(robotoLight);
            passwordEdit.setTypeface(robotoLight);

            newAc = (LinearLayout) rootView.findViewById(R.id.loginLinearLayout);
            existingAc = (LinearLayout) rootView.findViewById(R.id.existingAccountLinLayout);
            loading = (RelativeLayout) rootView.findViewById(R.id.LoadingRelLayout);
            if (viewstate == 0) {
                newAc.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.INVISIBLE);
            } else if (viewstate == 1) {
                newAc.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.VISIBLE);
            }

            List<String> usernames = new ArrayList<String>();
            for (String[] s : accs) {
                usernames.add(s[0]);
            }

            accountListAdapter adapter = new accountListAdapter(rootView.getContext(), R.layout.simple, usernames);
            accountsList.setAdapter(adapter);

            accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Timetable.started = false;
                    username = accs.get(position)[0];
                    getFriendsList l = new getFriendsList(rootView.getContext(), view, accs.get(position)[2]);
                    l.execute();

                }
            });
            return rootView;
        }
    }

    public void onSubmit(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        EditText passwordEdit = (EditText) findViewById(R.id.password);
        View root = view.getRootView();

        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        this.username = username;

        String ProcessLoginForm = "true";
        String signin = "Sign In";
        GetSymondsTimetable post = new GetSymondsTimetable(this, root);
        post.execute(ProcessLoginForm, username, password, signin);
    }

    public void onNew(View view) {
        loading.setVisibility(View.INVISIBLE);
        newAc.setVisibility(View.VISIBLE);
        existingAc.setVisibility(View.INVISIBLE);
    }
}

class accountListAdapter extends ArrayAdapter<String> {
    List<String> objects;
    Context context;
    LayoutInflater mInflater;

    public accountListAdapter(Context context, int resource, List<String> objects) {
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
        Typeface robotoLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(robotoLight);
        tv.setText(name);
        ImageView i = (ImageView) itemView.findViewById(R.id.imageView);

        i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_arrow));

        return itemView;
    }
}

class getFriendsList extends AsyncTask<Void, Void, Void> {

    Context ctx;
    View view;
    String html;
    ImageView i;
    ProgressBar p;
    boolean pinged = true;
    boolean success = true;

    public getFriendsList(Context ctx, View view, String html) {
        this.view = view;
        this.ctx = ctx;
        this.html = html;
        if(view!=null) {
            i = (ImageView) view.findViewById(R.id.imageView);
            p = (ProgressBar) view.findViewById(R.id.LoginprogressBar);
        }
    }

    protected void onPreExecute() {
        if(view!=null) {
            i.setVisibility(View.INVISIBLE);
            p.setVisibility(View.VISIBLE);
        }
    }

    protected Void doInBackground(Void... params) {
        Client client = new OrchestrateClient("3e21631e-63cf-4b9e-b227-beabb7eab90a");
            try {
                client.ping();
                pinged = true;
            } catch (Throwable e) {
                e.printStackTrace();
                pinged = false;
            }
        if(pinged){
            try {
                Iterable<KvObject<FriendDatabaseObject>> results =
                        client.relation("Frees", LoginScreen.username)
                                .get(FriendDatabaseObject.class, "friends")
                                .get(10, TimeUnit.SECONDS);
                for (KvObject<FriendDatabaseObject> i : results) {
                    AddAFriend_Activity.friends.add(new FriendList(i.getKey(), i.getValue()));
                }
                success = true;
            } catch (Throwable e){
                Log.e("myapp", e.toString());
                success = false;
            }
        }


        return null;
    }

    protected void onPostExecute(Void a) {
        if (!pinged) {
            Toast.makeText(ctx, "Not connected to the internet. Must be online to see Friends.", Toast.LENGTH_LONG).show();
            LoginScreen.offlinemode = true;
        }
        if (!success) {
            Toast.makeText(ctx, "Error Getting Friends.", Toast.LENGTH_LONG).show();
        }
        if(view!=null){
            i.setVisibility(View.VISIBLE);
            p.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(ctx, Timetable.class);
        intent.putExtra("timetableHTML", html);
        ctx.startActivity(intent);
    }
}





