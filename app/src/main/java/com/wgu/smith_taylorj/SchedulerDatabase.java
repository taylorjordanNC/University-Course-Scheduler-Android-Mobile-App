package com.wgu.smith_taylorj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.strictmode.InstanceCountViolation;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {Term.class, Course.class, CourseNote.class, Assessment.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class SchedulerDatabase extends RoomDatabase {
    private static SchedulerDatabase INSTANCE;

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract CourseNoteDao courseNoteDao();
    public abstract AssessmentDao assessmentDao();

    public static synchronized SchedulerDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, SchedulerDatabase.class, "schedulerdatabase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void populate(){
        insertTerms();
        insertCourses();
        insertCourseNotes();
        insertAssessments();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insertTerms(){
        Term exampleTerm1;
        Term exampleTerm2;
        Term exampleTerm3;

        Calendar start;
        Calendar end;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, 1);
        exampleTerm1 = new Term("Spring 2021", start.getTime(), end.getTime());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 5);
        exampleTerm2 = new Term("Fall 2021", start.getTime(), end.getTime());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 6);
        end.add(Calendar.MONTH, 9);
        exampleTerm3 = new Term("Spring 2022", start.getTime(), end.getTime());

        INSTANCE.termDao().addTerm(exampleTerm1);
        INSTANCE.termDao().addTerm(exampleTerm2);
        INSTANCE.termDao().addTerm(exampleTerm3);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insertCourses(){
        Course exampleCourse1;
        Course exampleCourse2;
        Course exampleCourse3;

        Calendar start;
        Calendar end;
        List<Term> termList = INSTANCE.termDao().getAllTerms();
        if(termList == null) return;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, 1);
        exampleCourse1 = new Course("Physics I", start.getTime(), end.getTime(), "In Progress", "Professor X", "x@school.edu", "919-111-1111", termList.get(0).getId());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 5);
        exampleCourse2 = new Course("Biology II", start.getTime(), end.getTime(), "Plan to take", "Professor Y", "y@school.edu", "919-222-2222", termList.get(1).getId());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 6);
        end.add(Calendar.MONTH, 9);
        exampleCourse3 = new Course("Calculus I", start.getTime(), end.getTime(), "Plan to take", "Professor Z", "z@school.edu", "919-333-3333", termList.get(2).getId());

        INSTANCE.courseDao().addCourse(exampleCourse1);
        INSTANCE.courseDao().addCourse(exampleCourse2);
        INSTANCE.courseDao().addCourse(exampleCourse3);
    }

    public void insertCourseNotes(){
        CourseNote exampleCourseNote1;
        CourseNote exampleCourseNote2;
        CourseNote exampleCourseNote3;

        List<Course> courseList = INSTANCE.courseDao().getAllCourses();
        if(courseList == null) return;

        exampleCourseNote1 = new CourseNote("Example Note 1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", courseList.get(0).getId());
        exampleCourseNote2 = new CourseNote("Example Note 2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", courseList.get(1).getId());
        exampleCourseNote3 = new CourseNote("Example Note 3", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", courseList.get(2).getId());

        INSTANCE.courseNoteDao().addCourseNote(exampleCourseNote1);
        INSTANCE.courseNoteDao().addCourseNote(exampleCourseNote2);
        INSTANCE.courseNoteDao().addCourseNote(exampleCourseNote3);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insertAssessments(){
        Assessment exampleAssessment1;
        Assessment exampleAssessment2;
        Assessment exampleAssessment3;

        List<Course> courseList = INSTANCE.courseDao().getAllCourses();
        if(courseList == null) return;
        Calendar start;
        Calendar end;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -2);
        exampleAssessment1 = new Assessment("Test 1", start.getTime(), end.getTime(), courseList.get(0).getId(), "Performance");

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 2);
        exampleAssessment2 = new Assessment("Midterm", start.getTime(), start.getTime(), courseList.get(1).getId(), "Objective");

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 9);
        end.add(Calendar.MONTH, 9);
        exampleAssessment3 = new Assessment("Final Exam", start.getTime(), end.getTime(), courseList.get(2).getId(), "Performance");
        INSTANCE.assessmentDao().addAssessment(exampleAssessment1);
        INSTANCE.assessmentDao().addAssessment(exampleAssessment2);
        INSTANCE.assessmentDao().addAssessment(exampleAssessment3);
    }

}
