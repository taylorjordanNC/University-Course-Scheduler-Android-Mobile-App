package com.wgu.smith_taylorj;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Course.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE
        )},
        indices = {@Index(value = "id")})
public class CourseNote {
    @PrimaryKey(autoGenerate = true)
    long id;
    private String noteTitle;
    private String note;
    private long courseId;

    @Ignore
    public CourseNote(){}

    public CourseNote(String noteTitle, String note, long courseId) {
        this.noteTitle = noteTitle;
        this.note = note;
        this.courseId = courseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
}
