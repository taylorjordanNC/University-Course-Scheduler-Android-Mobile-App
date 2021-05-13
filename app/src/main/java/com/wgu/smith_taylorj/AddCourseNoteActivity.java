package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddCourseNoteActivity extends AppCompatActivity {
    SchedulerDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_note);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        long courseId = getIntent().getLongExtra("courseId", -1);

        EditText addTitle = findViewById(R.id.addNoteTitle);
        EditText addNote = findViewById(R.id.addNoteContent);
        FloatingActionButton returnBtn = findViewById(R.id.returnToNotesBtn);
        Button addNoteBtn = findViewById(R.id.addNoteBtn);

        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(AddCourseNoteActivity.this, CourseNotesActivity.class);
            returnIntent.putExtra("courseId", courseId);
            startActivity(returnIntent);
        });

        addNoteBtn.setOnClickListener(v -> {
            CourseNote newNote = new CourseNote();
            newNote.setCourseId(courseId);
            if(!addTitle.getText().toString().isEmpty()){
                newNote.setNoteTitle(addTitle.getText().toString());
            } else newNote.setNoteTitle("New Note");
            if(!addNote.getText().toString().isEmpty()){
                newNote.setNote(addNote.getText().toString());
            } else newNote.setNote("");

            database.courseNoteDao().addCourseNote(newNote);
            Intent addedNoteIntent = new Intent(AddCourseNoteActivity.this, CourseNotesActivity.class);
            addedNoteIntent.putExtra("courseId", courseId);
            startActivity(addedNoteIntent);
        });
    }
}