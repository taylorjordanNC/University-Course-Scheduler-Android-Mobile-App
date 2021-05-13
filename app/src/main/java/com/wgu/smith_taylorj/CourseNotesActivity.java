package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CourseNotesActivity extends AppCompatActivity {
    ListView lv;
    SchedulerDatabase database;
    Course selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_notes);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long selectedCourseId = getIntent().getLongExtra("courseId", -1);
        selectedCourse = database.courseDao().getCourseById(selectedCourseId);

        lv = findViewById(R.id.notesListView);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent selectNoteIntent = new Intent(getApplicationContext(), CourseNoteDetailsActivity.class);
            long coursenote_id;
            List<CourseNote> courseNoteList = database.courseNoteDao().getCourseNotesByCourseId(selectedCourseId);
            coursenote_id = courseNoteList.get(pos).getId();
            selectNoteIntent.putExtra("coursenote_id", coursenote_id);
            startActivity(selectNoteIntent);
        });
        updateList();

        TextView titleTextView = findViewById(R.id.notesHeader);
        String string = "Notes for ";
        titleTextView.setText(string.concat(selectedCourse.getTitle()));

        FloatingActionButton returnToCourseBtn = findViewById(R.id.returnToCourseBtn);
        returnToCourseBtn.setOnClickListener(v -> {
            Intent returnToCourseIntent = new Intent(CourseNotesActivity.this, CourseDetailsActivity.class);
            returnToCourseIntent.putExtra("courseId", selectedCourseId);
            startActivity(returnToCourseIntent);
        });

        FloatingActionButton addCourseNote = findViewById(R.id.addCourseNoteBtn);
        addCourseNote.setOnClickListener(v -> {
            Intent addNoteIntent = new Intent(CourseNotesActivity.this, AddCourseNoteActivity.class);
            addNoteIntent.putExtra("courseId", selectedCourseId);
            startActivity(addNoteIntent);
        });
    }


    private void updateList(){
        List<CourseNote> courseNoteList = database.courseNoteDao().getCourseNotesByCourseId(selectedCourse.getId());
        String[] items = new String[courseNoteList.size()];
        if(!courseNoteList.isEmpty()){
            for(int i = 0; i < courseNoteList.size(); i++){
                items[i] = courseNoteList.get(i).getNoteTitle();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}