package com.example.asaf_avisar;

public class Lesson {
    private String lessonType;
    private String date;
    private String time;
    private boolean isPaid;

    public Lesson() {
        // Default constructor for Firebase
    }

    public Lesson(String lessonType, String date, String time, boolean isPaid) {
        this.lessonType = lessonType;
        this.date = date;
        this.time = time;
        this.isPaid = isPaid;
    }

    public String getLessonType() { return lessonType; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public boolean isPaid() { return isPaid; }

    public void setPaid(boolean paid) { isPaid = paid; }
}
