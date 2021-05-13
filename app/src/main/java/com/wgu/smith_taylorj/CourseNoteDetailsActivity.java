package com.wgu.smith_taylorj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CourseNoteDetailsActivity extends AppCompatActivity {
    SchedulerDatabase database;
    CourseNote selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_details);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long noteId = getIntent().getLongExtra("coursenote_id", -1);
        selectedNote = database.courseNoteDao().getCourseNoteById(noteId);

        FloatingActionButton shareBtn = findViewById(R.id.shareNoteBtn);
        FloatingActionButton editBtn = findViewById(R.id.editNoteBtn);
        FloatingActionButton deleteBtn = findViewById(R.id.deleteNoteBtn);
        FloatingActionButton backBtn = findViewById(R.id.backToCourseNotesBtn);
        TextView title = findViewById(R.id.noteTitleTextView);
        TextView noteContent = findViewById(R.id.noteContentTextView);

        shareBtn.setOnClickListener(v -> {
            Intent sendNote = new Intent();
            sendNote.setAction(Intent.ACTION_SEND);
            sendNote.putExtra(Intent.EXTRA_TEXT, selectedNote.getNote());
            sendNote.putExtra(Intent.EXTRA_TITLE, selectedNote.getNoteTitle());
            sendNote.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendNote, null);
            startActivity(shareIntent);
        });
        editBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(CourseNoteDetailsActivity.this, EditCourseNoteActivity.class);
            editIntent.putExtra("noteId", noteId);
            startActivity(editIntent);
        });
        deleteBtn.setOnClickListener(v -> {
                    new AlertDialog.Builder(CourseNoteDetailsActivity.this)
                    .setTitle("Delete Course Note")
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Intent deleteIntent = new Intent(CourseNoteDetailsActivity.this, CourseNotesActivity.class);
                        deleteIntent.putExtra("courseId", selectedNote.getCourseId());
                        database.courseNoteDao().removeCourseNote(noteId);
                        startActivity(deleteIntent);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
        backBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(CourseNoteDetailsActivity.this, CourseNotesActivity.class);
            returnIntent.putExtra("courseId", selectedNote.getCourseId());
            startActivity(returnIntent);
        });

        title.setText(selectedNote.getNoteTitle());
        noteContent.setText(selectedNote.getNote());
    }
}