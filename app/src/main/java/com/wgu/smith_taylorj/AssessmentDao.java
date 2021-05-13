package com.wgu.smith_taylorj;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert
    void addAssessment(Assessment assessment);

    @Query("select * from assessment")
    List<Assessment> getAllAssessments();

    @Query("select * from assessment where courseId = :id")
    List<Assessment> getAssessmentsByCourseId(long id);

    @Query("select * from assessment where id = :assessmentId")
    Assessment getAssessmentById(long assessmentId);

    @Update
    void updateAssessment(Assessment assessment);

    @Query("delete from assessment")
    void removeAllAssessments();

    @Query("delete from assessment where id = :assessmentId")
    void removeAssessment(long assessmentId);
}
