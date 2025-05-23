package com.example.asaf_avisar.objects;

/**
 * The type Lesson.
 */
public class Lesson {
    private String lessonId;
    private String lessonType;
    private String date;
    private String time;
    private boolean isPaid;

    /**
     * Instantiates a new Lesson.
     */
    public Lesson() {
        // Required empty constructor for Firebase
    }

    /**
     * Instantiates a new Lesson.
     *
     * @param lessonType the lesson type
     * @param date       the date
     * @param time       the time
     * @param isPaid     the is paid
     */
    public Lesson(String lessonType, String date, String time, boolean isPaid) {
        this.lessonType = lessonType;
        this.date = date;
        this.time = time;
        this.isPaid = isPaid;
    }

    /**
     * Gets lesson id.
     *
     * @return the lesson id
     */
    public String getLessonId() { return lessonId; }

    /**
     * Sets lesson id.
     *
     * @param lessonId the lesson id
     */
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    /**
     * Gets lesson type.
     *
     * @return the lesson type
     */
    public String getLessonType() { return lessonType; }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() { return date; }

    /**
     * Gets time.
     *
     * @return the time
     */
    public String getTime() { return time; }

    /**
     * Is paid boolean.
     *
     * @return the boolean
     */
    public boolean isPaid() { return isPaid; }

    /**
     * Sets paid.
     *
     * @param paid the paid
     */
    public void setPaid(boolean paid) { isPaid = paid; }
}
