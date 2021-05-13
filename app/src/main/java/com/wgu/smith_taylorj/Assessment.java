package com.wgu.smith_taylorj;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Course.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "id")})
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    long id;
    private String title;
    private Date startDate;
    private Date endDate;
    private long courseId;
    private String type;
    private boolean notifyStart;
    private boolean notifyEnd;

    @Ignore
    public Assessment(){}

    public Assessment(String title, Date startDate, Date endDate, long courseId, String type) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseId = courseId;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNotifyStart() {
        return notifyStart;
    }

    public void setNotifyStart(boolean notifyStart) {
        this.notifyStart = notifyStart;
    }

    public boolean isNotifyEnd() {
        return notifyEnd;
    }

    public void setNotifyEnd(boolean notifyEnd) {
        this.notifyEnd = notifyEnd;
    }
}
