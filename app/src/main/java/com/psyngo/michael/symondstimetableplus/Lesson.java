package com.psyngo.michael.symondstimetableplus;

/**
 * Created by Michael on 19/10/2014.
 */
public class Lesson {
    private String lessonTime;
    private String lessonName;
    private String lessonTeacher;
    private String lessonRoom;
    private String length;

    public Lesson(String time, String subjectName, String teacher, String room, String length){
        this.lessonTime = time;
        this.lessonName = subjectName;
        this.lessonTeacher = teacher;
        this.lessonRoom = room;
        this.length = length;
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
}
