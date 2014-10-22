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
    private String length;
    private Calendar startTime;
    private Calendar endTime;

    public Lesson(String time, String subjectName, String teacher, String room, String length, Calendar startTime, Calendar endTime){
        this.lessonTime = time;
        this.lessonName = subjectName;
        this.lessonTeacher = teacher;
        this.lessonRoom = room;
        this.length = length;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getLength() {
        return length;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }
}
