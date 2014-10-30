package com.psyngo.michael.symondstimetableplus;

import java.util.Calendar;

/**
 * Created by Michael on 19/10/2014.
 */
public class Lesson {
    private String lessonTime;
    private String lessonName;
    private String lessonTeacher;
    private String lessonRoom;
    private int length;
    private Calendar startTime;
    private Calendar endTime;
    private String nextTime;
    private String whoElseFree;
    private int backgroundColor;

    public Lesson(String time, String subjectName, String teacher, String room, int length, String nextTime, String whoElseFree, Calendar startTime, Calendar endTime, int backgroundColor){
        this.lessonTime = time;
        this.lessonName = subjectName;
        this.lessonTeacher = teacher;
        this.lessonRoom = room;
        this.length = length;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nextTime = nextTime;
        this.whoElseFree = whoElseFree;
        this.backgroundColor = backgroundColor;
    }

    public String getLessonTime() {
        return lessonTime;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getLessonTeacher() {
        return lessonTeacher;
    }

    public String getLessonRoom() {
        return lessonRoom;
    }

    public String getNextTime() {
        return nextTime;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLength() {
        return length;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getWhoElseFree() {
        return whoElseFree;
    }
}
