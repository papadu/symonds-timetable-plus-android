package com.psyngo.michael.symondstimetableplus;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by michael on 04/01/15.
 */
public class FriendObjectConverter {
    public FriendDatabaseObject convertToFriendDatabaseObject(FriendJsonObject x){
        FriendDatabaseObject v = new FriendDatabaseObject();
        v.setName(x.name);
        v.setDate(x.date);

        v.setMonday(convertToCalendarArray(x.monday));
        v.setTuesday(convertToCalendarArray(x.tuesday));
        v.setWednesday(convertToCalendarArray(x.wednesday));
        v.setThursday(convertToCalendarArray(x.thursday));
        v.setFriday(convertToCalendarArray(x.friday));

        return v;
    }

    public FriendJsonObject convertToFriendJsonObject(FriendDatabaseObject x){
        FriendJsonObject result = new FriendJsonObject();
        result.name = x.getName();
        result.date = x.getDate();
        result.monday = convertToNumberArray(x.getMonday());
        result.tuesday = convertToNumberArray(x.getTuesday());
        result.wednesday = convertToNumberArray(x.getWednesday());
        result.thursday = convertToNumberArray(x.getThursday());
        result.friday = convertToNumberArray(x.getFriday());

        return  result;
    }

    private List<Calendar[]> convertToCalendarArray(ArrayList<Number[]> x){
        List<Calendar[]> result = new ArrayList<>();
        for(Number[] free : x) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            try {
                start.setTime(new SimpleDateFormat("HH:mm").parse("00:00"));
                end.setTime(new SimpleDateFormat("HH:mm").parse("00:00"));
            } catch (ParseException e){
                e.printStackTrace();
            }

            //no idea why you have to add an hour, but you do.
            start.add(Calendar.HOUR, 1);
            end.add(Calendar.HOUR, 1);

            start.add(Calendar.MILLISECOND, free[0].intValue());
            end.add(Calendar.MILLISECOND, free[1].intValue());

            Calendar[] cals = new Calendar[2];
            cals[0] = start;
            cals[1] = end;
            result.add(cals);
        }
        return result;
    }

    private ArrayList<Number[]> convertToNumberArray(List<Calendar[]> x){
        ArrayList<Number[]> result = new ArrayList<>();
        for(Calendar[] free : x){
            Number first = free[0].getTimeInMillis();
            Number second = free[1].getTimeInMillis();

            Number[] nums = new Number[2];
            nums[0] = first;
            nums[1] = second;
            result.add(nums);
        }
        return result;
    }
}
