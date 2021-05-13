package com.wgu.smith_taylorj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EditCourseActivity extends AppCompatActivity {
    SchedulerDatabase database;
    Course selectedCourse;
    ListView lv;
    Calendar startCalendar;
    Calendar endCalendar;
    TextView startDate;
    TextView endDate;
    CheckBox notifyStart;
    CheckBox notifyEnd;
    String myFormat;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long courseId = getIntent().getLongExtra("courseId", -1);
        selectedCourse = database.courseDao().getCourseById(courseId);

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        notifyStart = findViewById(R.id.editCourseNotifyStart);
        notifyEnd = findViewById(R.id.editCourseNotifyEnd);

        lv = findViewById(R.id.editCourseAssessLV);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent selectAssessmentIntent = new Intent(EditCourseActivity.this, AssessmentDetailsActivity.class);
            long assessment_id;
            List<Assessment> assessmentList = database.assessmentDao().getAssessmentsByCourseId(selectedCourse.getId());
            assessment_id = assessmentList.get(pos).getId();
            selectAssessmentIntent.putExtra("assessmentId", assessment_id);
            startActivity(selectAssessmentIntent);
        });
        updateList();

        EditText editTitle = findViewById(R.id.editCourseTitle);
        startDate = findViewById(R.id.editCourseStart);
        endDate = findViewById(R.id.editCourseEnd);
        EditText editStatus = findViewById(R.id.editCourseStatus);
        EditText editInstructorName = findViewById(R.id.editInstructorName);
        EditText editInstructorEmail = findViewById(R.id.editInstructorEmail);
        EditText editInstructorPhone = findViewById(R.id.editInstructorPhone);

        editTitle.setText(selectedCourse.getTitle());
        startDate.setText(sdf.format(selectedCourse.getStartDate()));
        endDate.setText(sdf.format(selectedCourse.getEndDate()));
        editStatus.setText(selectedCourse.getStatus());
        editInstructorName.setText(selectedCourse.getInstructorName());
        editInstructorEmail.setText(selectedCourse.getInstructorEmail());
        editInstructorPhone.setText(selectedCourse.getInstructorPhone());

        Button notesBtn = findViewById(R.id.editCourseNotesBtn);
        notesBtn.setOnClickListener(v -> {
            Intent notesIntent = new Intent(EditCourseActivity.this, CourseNotesActivity.class);
            notesIntent.putExtra("courseId", courseId);
            startActivity(notesIntent);
        });

        FloatingActionButton returnBtn = findViewById(R.id.editCourseReturnToDetailsBtn);
        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(EditCourseActivity.this, CourseDetailsActivity.class);
            returnIntent.putExtra("courseId", courseId);
            startActivity(returnIntent);
        });

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedCourse.getStartDate());
        endCalendar.setTime(selectedCourse.getEndDate());
        DatePickerDialog.OnDateSetListener myStartDate = (view, year, monthOfYear, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateStartLabel();
        };
        DatePickerDialog.OnDateSetListener myEndDate = (view, year, monthOfYear, dayOfMonth) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEndLabel();
        };

        startDate.setOnClickListener(v -> new DatePickerDialog(EditCourseActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endDate.setOnClickListener(v -> new DatePickerDialog(EditCourseActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Random random = new Random();
        FloatingActionButton saveBtn = findViewById(R.id.editCourseSaveBtn);
        saveBtn.setOnClickListener(v -> {
            selectedCourse.setTitle(editTitle.getText().toString());
            try {
                selectedCourse.setStartDate(sdf.parse(startDate.getText().toString()));
                selectedCourse.setEndDate(sdf.parse(endDate.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            selectedCourse.setStatus(editStatus.getText().toString());
            selectedCourse.setInstructorName(editInstructorName.getText().toString());
            selectedCourse.setInstructorEmail(editInstructorEmail.getText().toString());
            selectedCourse.setInstructorPhone(editInstructorPhone.getText().toString());

            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(EditCourseActivity.this, MyReceiver.class);
                startIntent.putExtra("toastMsg", selectedCourse.getTitle() + " starting with ID: " + selectedCourse.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(EditCourseActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                selectedCourse.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(EditCourseActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", selectedCourse.getTitle() + " ending with ID: " + selectedCourse.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(EditCourseActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                selectedCourse.setNotifyEnd(true);
            }
            database.courseDao().updateCourse(selectedCourse);
            Toast toast = Toast.makeText(EditCourseActivity.this, "Changes Saved", Toast.LENGTH_LONG);
            toast.show();
        });

        Button addAssessment = findViewById(R.id.addAssessBtn);
        addAssessment.setOnClickListener(v -> {
            Intent addAssessmentIntent = new Intent(EditCourseActivity.this, AddAssessmentActivity.class);
            addAssessmentIntent.putExtra("courseId", selectedCourse.getId());
            startActivity(addAssessmentIntent);
        });

        if(selectedCourse.isNotifyStart()){
            notifyStart.setChecked(true);
        }
        if(selectedCourse.isNotifyEnd()){
            notifyEnd.setChecked(true);
        }
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


    private void updateStartLabel(){
        startDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndLabel(){
        endDate.setText(sdf.format(endCalendar.getTime()));
    }
}