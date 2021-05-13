package com.wgu.smith_taylorj;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;

@Entity(indices = {@Index(value = "id")})
public class Term {
    @PrimaryKey(autoGenerate = true)
    long id;
    private String title;
    private Date startDate;
    private Date endDate;
    private boolean notifyStart;
    private boolean notifyEnd;

    @Ignore
    public Term(){}

    public Term(String title, Date startDate, Date endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
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
