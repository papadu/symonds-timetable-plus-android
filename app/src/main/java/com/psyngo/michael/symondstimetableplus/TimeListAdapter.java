package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michael on 07/11/2014.
 */
public class TimeListAdapter extends ArrayAdapter<String> {

    List<String> objects;
    Context context;
    LayoutInflater mInflater;

    public TimeListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {


            itemView = mInflater.inflate(R.layout.list_item_times, parent, false);


        }
           String time = objects.get(position);
        LinearLayout container = (LinearLayout) itemView.findViewById(R.id.list_item_time_container_linearlayout);
        ViewGroup.LayoutParams contParams = container.getLayoutParams();
        TextView timeTextview = (TextView) itemView.findViewById(R.id.list_item_time_textview);
        contParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, context.getResources().getDisplayMetrics());

        if(time.equals("10:20")){
            timeTextview.setText("");

            contParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
        }
        else{
            timeTextview.setText(time);
        }
        Typeface robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        timeTextview.setTypeface(robotoThin);

        return itemView;
    }


}
