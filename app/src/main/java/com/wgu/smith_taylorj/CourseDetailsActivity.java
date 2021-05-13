package com.wgu.smith_taylorj;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CourseDetailsActivity extends AppCompatActivity {
    long courseId;
    Course selectedCourse;
    Term term;
    SchedulerDatabase database;
    String myFormat;
    java.text.SimpleDateFormat sdf;
    ListView lv;
    Calendar startCalendar;
    Calendar endCalendar;
    long startDate;
    long endDate;
    CheckBox notifyStart;
    CheckBox notifyEnd;
    boolean isNotifyStart;
    boolean isNotifyEnd;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        Intent intent = getIntent();
        courseId = intent.getLongExtra("courseId", -1);
        selectedCourse = database.courseDao().getCourseById(courseId);
        term = database.termDao().getTerm(selectedCourse.getTermId());

        notifyStart = findViewById(R.id.courseDetailsNotifyStart);
        notifyEnd = findViewById(R.id.courseDetailsNotifyEnd);
        isNotifyStart = selectedCourse.isNotifyStart();
        isNotifyEnd = selectedCourse.isNotifyEnd();

        if(selectedCourse.isNotifyStart()){
            notifyStart.setChecked(true);
        }
        if(selectedCourse.isNotifyEnd()){
            notifyEnd.setChecked(true);
        }
        notifyStart.setClickable(false);
        notifyEnd.setClickable(false);

        TextView courseTitle = findViewById(R.id.courseTitle);
        TextView startDate = findViewById(R.id.courseStart);
        TextView endDate = findViewById(R.id.courseEnd);
        TextView status = findViewById(R.id.courseStatus);
        TextView instructorName = findViewById(R.id.instructorName);
        TextView instructorEmail = findViewById(R.id.instructorEmail);
        TextView instructorPhone = findViewById(R.id.instructorPhone);

        courseTitle.setText(selectedCourse.getTitle());
        startDate.setText(sdf.format(selectedCourse.getStartDate()));
        endDate.setText(sdf.format(selectedCourse.getEndDate()));
        status.setText(selectedCourse.getStatus());
        instructorName.setText(selectedCourse.getInstructorName());
        instructorEmail.setText(selectedCourse.getInstructorEmail());
        instructorPhone.setText(selectedCourse.getInstructorPhone());

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton allCoursesBtn = findViewById(R.id.returnToTermBtn);
        FloatingActionButton editCourseBtn = findViewById(R.id.editCourseBtn);
        FloatingActionButton deleteCourseBtn = findViewById(R.id.deleteCourseBtn);

        allCoursesBtn.setOnClickListener(v -> {
            Intent allCoursesIntent = new Intent(CourseDetailsActivity.this, TermDetailsActivity.class);
            allCoursesIntent.putExtra("termId", term.getId());
            startActivity(allCoursesIntent);
        });
        editCourseBtn.setOnClickListener(v -> {
            Intent editCourseIntent = new Intent(CourseDetailsActivity.this, EditCourseActivity.class);
            editCourseIntent.putExtra("courseId", selectedCourse.getId());
            startActivity(editCourseIntent);
        });
        deleteCourseBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(CourseDetailsActivity.this)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Intent deleteIntent = new Intent(CourseDetailsActivity.this, TermDetailsActivity.class);
                        deleteIntent.putExtra("termId", selectedCourse.getTermId());
                        database.courseDao().removeCourse(selectedCourse.getId());
                        startActivity(deleteIntent);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        lv = findViewById(R.id.assessmentListView);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent selectAssessmentIntent = new Intent(CourseDetailsActivity.this, AssessmentDetailsActivity.class);
            long assessment_id;
            List<Assessment> assessmentList = database.assessmentDao().getAssessmentsByCourseId(selectedCourse.getId());
            assessment_id = assessmentList.get(pos).getId();
            selectAssessmentIntent.putExtra("assessmentId", assessment_id);
            startActivity(selectAssessmentIntent);
        });
        updateList();

        Button openNotesBtn = findViewById(R.id.notesBtn);
        openNotesBtn.setOnClickListener(v -> {
            Intent notesIntent = new Intent(CourseDetailsActivity.this, CourseNotesActivity.class);
            notesIntent.putExtra("courseId", selectedCourse.getId());
            startActivity(notesIntent);
        });

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedCourse.getStartDate());
        endCalendar.setTime(selectedCourse.getEndDate());
    }

    private void updateList(){
        List<Assessment> courseAssessments = database.assessmentDao().getAssessmentsByCourseId(selectedCourse.getId());
        String[] items = new String[courseAssessments.size()];
        if(!courseAssessments.isEmpty()){
            for(int i = 0; i < courseAssessments.size(); i++){
                items[i] = courseAssessments.get(i).getTitle();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_coursedetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Random random = new Random();
        int id = item.getItemId();
        switch(id){
            case R.id.notifyAtStart:
                if(!isNotifyStart) {
                    Intent startIntent = new Intent(CourseDetailsActivity.this, MyReceiver.class);
                    startIntent.putExtra("toastMsg", selectedCourse.getTitle() + " starting with ID: " + selectedCourse.getId());
                    PendingIntent senderStart = PendingIntent.getBroadcast(CourseDetailsActivity.this, random.nextInt(), startIntent, 0);
                    AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    startDate = startCalendar.getTimeInMillis();
                    alarmManagerStart.set(AlarmManager.RTC_WAKEUP, startDate, senderStart);
                    selectedCourse.setNotifyStart(true);
                    database.courseDao().updateCourse(selectedCourse);
                    notifyStart.setChecked(true);
                    return true;
                } else {
                    Toast alertIsNotifying = Toast.makeText(getApplicationContext(), "Notification for start already set", Toast.LENGTH_LONG);
                    alertIsNotifying.show();
                }
            case R.id.notifyAtEnd:
                if(!isNotifyEnd){
                Intent endIntent = new Intent(CourseDetailsActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", selectedCourse.getTitle() + " ending with ID: " + selectedCourse.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(CourseDetailsActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                endDate = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, endDate, senderEnd);
                selectedCourse.setNotifyEnd(true);
                database.courseDao().updateCourse(selectedCourse);
                notifyEnd.setChecked(true);
                return true;
                } else {
                    Toast alertIsNotifying = Toast.makeText(getApplicationContext(), "Notification for end already set", Toast.LENGTH_LONG);
                    alertIsNotifying.show();
                }
            default: return super.onOptionsItemSelected(item);
        }
    }
}