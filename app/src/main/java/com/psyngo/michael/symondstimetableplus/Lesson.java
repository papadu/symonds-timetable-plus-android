package com.psyngo.michael.symondstimetableplus;

import java.util.Calendar;
import java.util.List;

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
    private List<String> whoElseFree;
    private int backgroundColor;

    public void setLessonTime(String lessonTime) {
        this.lessonTime = lessonTime;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setLessonTeacher(String lessonTeacher) {
        this.lessonTeacher = lessonTeacher;
    }

    public void setLessonRoom(String lessonRoom) {
        this.lessonRoom = lessonRoom;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setWhoElseFree(List<String> whoElseFree) {
        this.whoElseFree = whoElseFree;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Lesson(String time, String subjectName, String teacher, String room, int length, List<String> whoElseFree, Calendar startTime, Calendar endTime, int backgroundColor){
        this.lessonTime = time;
        this.lessonName = subjectName;
        this.lessonTeacher = teacher;
        this.lessonRoom = room;
        this.length = length;

        this.startTime = startTime;
        this.endTime = endTime;
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

    public List<String> getWhoElseFree() {
        return whoElseFree;
    }
}
