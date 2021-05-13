package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditCourseNoteActivity extends AppCompatActivity {
    SchedulerDatabase database;
    CourseNote selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_note);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long noteId = getIntent().getLongExtra("noteId", -1);
        selectedNote = database.courseNoteDao().getCourseNoteById(noteId);

        EditText noteTitle = findViewById(R.id.noteTitleTextView);
        EditText noteContent = findViewById(R.id.noteContentTextView);
        FloatingActionButton cancelBtn = findViewById(R.id.cancelEditNoteBtn);
        FloatingActionButton saveBtn = findViewById(R.id.saveNoteChangesBtn);

        noteTitle.setText(selectedNote.getNoteTitle());
        noteContent.setText(selectedNote.getNote());

        cancelBtn.setOnClickListener(v -> {
            Intent cancelIntent = new Intent(EditCourseNoteActivity.this, CourseNoteDetailsActivity.class);
            cancelIntent.putExtra("coursenote_id", noteId);
            startActivity(cancelIntent);
        });
        saveBtn.setOnClickListener(v -> {
            if(!noteTitle.getText().toString().isEmpty()){
                selectedNote.setNoteTitle(noteTitle.getText().toString());
            } else selectedNote.setNoteTitle("Empty Title");
            if(!noteContent.getText().toString().isEmpty()){
                selectedNote.setNote(noteContent.getText().toString());
            } else selectedNote.setNote("");
            database.courseNoteDao().updateCourseNote(selectedNote);
            Toast toast = Toast.makeText(EditCourseNoteActivity.this, "Changes Saved", Toast.LENGTH_LONG);
            toast.show();
        });
    }
}