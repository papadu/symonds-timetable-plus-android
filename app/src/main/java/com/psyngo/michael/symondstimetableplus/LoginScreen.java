package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class LoginScreen extends ActionBarActivity {

    static LinearLayout newAc;
    static LinearLayout existingAc;
    static RelativeLayout loading;
    DataHandler db;
    static public int viewstate = 0;
    static List<String[]> accs = new ArrayList<String[]>();
    static String username;

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
        db = new DataHandler(getBaseContext());
        db.open();
        Cursor C = db.returnData();
        if(C.moveToFirst()){
            viewstate = 1;
            do{
                user = C.getString(0);
                pass = C.getString(1);
                html = C.getString(2);
                accs.add(new String[]{user, pass, html});

            }
            while(C.moveToNext());
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
            if(viewstate == 0){
                newAc.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.INVISIBLE);
            }
            else if (viewstate == 1){
                newAc.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.VISIBLE);
            }

            List<String> usernames = new ArrayList<String>();
            for(String[] s : accs){
                usernames.add(s[0]);
            }

            accountListAdapter adapter = new accountListAdapter(rootView.getContext(), R.layout.simple, usernames);
            accountsList.setAdapter(adapter);

            accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Timetable.started = false;
                    username = accs.get(position)[0];
                    Intent intent = new Intent(rootView.getContext(), Timetable.class);
                    intent.putExtra("timetableHTML", accs.get(position)[2]);
                    rootView.getContext().startActivity(intent);





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

    public void onNew(View view){
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





