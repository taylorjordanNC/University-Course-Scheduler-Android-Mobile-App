package com.wgu.smith_taylorj;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TermDao {

    @Insert
    void addTerm(Term term);

    @Query("select * from term")
    List<Term> getAllTerms();

    @Query("select * from term where id = :termId")
    Term getTerm(long termId);

    @Update
    void updateTerm(Term term);

    @Query("delete from term where id = :termId")
    void removeTerm(long termId);

    @Query("delete from term")
    void removeAllTerms();
}
