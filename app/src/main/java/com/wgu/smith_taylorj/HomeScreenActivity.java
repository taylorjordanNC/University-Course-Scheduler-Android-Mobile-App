package com.wgu.smith_taylorj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.time.Instant;
import java.util.Date;

public class HomeScreenActivity extends AppCompatActivity{
    private SchedulerDatabase database;
    private Term currentTerm;
    private static boolean alreadyExecuted;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        if(!alreadyExecuted){
            database.termDao().removeAllTerms();
            database.courseDao().removeAllCourses();
            database.assessmentDao().removeAllAssessments();
            database.courseNoteDao().removeAllCourseNotes();
            database.populate();
            alreadyExecuted = true;
        }

        Button currentTermBtn = findViewById(R.id.currentTermBtn);
        currentTermBtn.setOnClickListener(v -> {
            Term currentTerm = null;
            Intent allTermsIntent = new Intent(HomeScreenActivity.this, TermDetailsActivity.class);
            Date currentDate = Date.from(Instant.now());
            for(Term t : database.termDao().getAllTerms()){
                if(t.getStartDate().before(currentDate) && t.getEndDate().after(currentDate)){
                    currentTerm = t;
                }
            }
            if(currentTerm != null){
                allTermsIntent.putExtra("termId", currentTerm.getId());
            } else {
                Toast toast = new Toast(HomeScreenActivity.this);
                toast.makeText(getApplicationContext(), "No terms are currently active.", Toast.LENGTH_LONG);
                toast.show();
            }
            startActivity(allTermsIntent);
        });


        Button allTermsBtn = findViewById(R.id.allTermsBtn);
        allTermsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, TermListActivity.class);
            startActivity(intent);
        });

        getSupportActionBar().setTitle("Student Schedule App");


    }

}