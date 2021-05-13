package com.wgu.smith_taylorj;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CourseDao {

    @Insert
    void addCourse(Course course);

    @Query("select * from course")
    List<Course> getAllCourses();

    @Query("select * from course where termId = :id")
    List<Course> getCoursesByTermId(long id);

    @Query("select * from course where id = :courseId")
    Course getCourseById(long courseId);

    @Update
    void updateCourse(Course course);

    @Query("delete from course where id = :courseId")
    void removeCourse(long courseId);

    @Query("delete from course")
    void removeAllCourses();
}
