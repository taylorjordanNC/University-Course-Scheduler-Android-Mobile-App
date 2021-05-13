package com.wgu.smith_taylorj;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Term.class,
                parentColumns = "id",
                childColumns = "termId",
                onDelete = ForeignKey.CASCADE
        )},
        indices = {@Index(value = "id")}
)
public class Course {
    @PrimaryKey(autoGenerate = true)
    long id;
    private String title;
    private Date startDate;
    private Date endDate;
    private String status;
    private String instructorName;
    private String instructorPhone;
    private String instructorEmail;
    private long termId;
    @Ignore
    private boolean showMenu;
    private boolean notifyStart;
    private boolean notifyEnd;

    @Ignore
    public Course(){}

    public Course(String title, Date startDate, Date endDate, String status, String instructorName, String instructorEmail,
                  String instructorPhone, long termId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.instructorName = instructorName;
        this.instructorPhone = instructorPhone;
        this.instructorEmail = instructorEmail;
        this.termId = termId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorPhone() {
        return instructorPhone;
    }

    public void setInstructorPhone(String instructorPhone) {
        this.instructorPhone = instructorPhone;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
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
