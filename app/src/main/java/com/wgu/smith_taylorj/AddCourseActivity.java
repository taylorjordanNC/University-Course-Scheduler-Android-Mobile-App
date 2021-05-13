package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddCourseActivity extends AppCompatActivity {
    SchedulerDatabase database;
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
        setContentView(R.layout.activity_add_course);

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifyStart = findViewById(R.id.addCourseNotifyStart);
        notifyEnd = findViewById(R.id.addCourseNotifyEnd);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long termId = getIntent().getLongExtra("termId", -1);

        EditText title = findViewById(R.id.addCourseTitle);
        startDate = findViewById(R.id.addCourseStart);
        endDate = findViewById(R.id.addCourseEnd);
        EditText status = findViewById(R.id.addCourseStatus);
        EditText instructorName = findViewById(R.id.addInstructorName);
        EditText instructorPhone = findViewById(R.id.addInstructorPhone);
        EditText instructorEmail = findViewById(R.id.addInstructorEmail);

        FloatingActionButton returnBtn = findViewById(R.id.returnToTermDetailsBtn);
        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(AddCourseActivity.this, EditTermActivity.class);
            returnIntent.putExtra("termId", termId);
            startActivity(returnIntent);
        });

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

        startDate.setOnClickListener(v -> new DatePickerDialog(AddCourseActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endDate.setOnClickListener(v -> new DatePickerDialog(AddCourseActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Random random = new Random();
        Button addNewCourseBtn = findViewById(R.id.addNewCourseBtn);
        addNewCourseBtn.setOnClickListener(v -> {
            Course newCourse = new Course();
            newCourse.setTermId(termId);

            if(!title.getText().toString().isEmpty()){
                newCourse.setTitle(title.getText().toString());
            } else newCourse.setTitle("New Course");

            if(!startDate.getText().toString().isEmpty()){
                try {
                    newCourse.setStartDate(sdf.parse(startDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newCourse.setStartDate(Calendar.getInstance().getTime());

            if(!endDate.getText().toString().isEmpty()){
                try {
                    newCourse.setEndDate(sdf.parse(endDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newCourse.setEndDate(Calendar.getInstance().getTime());

            if(!status.getText().toString().isEmpty()){
                newCourse.setStatus(status.getText().toString());
            } else newCourse.setStatus("");

            if(!instructorName.getText().toString().isEmpty()){
                newCourse.setInstructorName(instructorName.getText().toString());
            } else newCourse.setInstructorName("Name");

            if(!instructorPhone.getText().toString().isEmpty()){
                newCourse.setInstructorPhone(instructorPhone.getText().toString());
            } else newCourse.setInstructorPhone("***-***-****");

            if(!instructorEmail.getText().toString().isEmpty()){
                newCourse.setInstructorEmail(instructorEmail.getText().toString());
            } else newCourse.setInstructorEmail("example@email.com");


            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(AddCourseActivity.this, MyReceiver.class);
                startIntent.setClass(AddCourseActivity.this, AddCourseActivity.class);
                startIntent.putExtra("toastMsg", newCourse.getTitle() + " starting with ID: " + newCourse.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(AddCourseActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                newCourse.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(AddCourseActivity.this, MyReceiver.class);
                endIntent.setClass(AddCourseActivity.this, AddCourseActivity.class);
                endIntent.putExtra("toastMsg", newCourse.getTitle() + " ending with ID: " + newCourse.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(AddCourseActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                newCourse.setNotifyEnd(true);
            }

            database.courseDao().addCourse(newCourse);

            Intent addedCourseIntent = new Intent(AddCourseActivity.this, EditTermActivity.class);
            addedCourseIntent.putExtra("termId", termId);
            startActivity(addedCourseIntent);
        });
    }

    private void updateStartLabel(){
        startDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndLabel(){
        endDate.setText(sdf.format(endCalendar.getTime()));
    }
}