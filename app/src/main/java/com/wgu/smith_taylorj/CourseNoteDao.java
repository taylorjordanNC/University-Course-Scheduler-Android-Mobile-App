package com.wgu.smith_taylorj;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CourseNoteDao {

    @Insert
    void addCourseNote(CourseNote courseNote);

    @Query("select * from coursenote")
    List<CourseNote> getAllCourseNotes();

    @Query("select * from coursenote where courseId = :id")
    List<CourseNote> getCourseNotesByCourseId(long id);

    @Query("select * from coursenote where id = :courseNoteId")
    CourseNote getCourseNoteById(long courseNoteId);

    @Update
    void updateCourseNote(CourseNote courseNote);

    @Query("delete from coursenote where id = :courseNoteId")
    void removeCourseNote(long courseNoteId);
    @Query("delete from coursenote")
    void removeAllCourseNotes();
}
