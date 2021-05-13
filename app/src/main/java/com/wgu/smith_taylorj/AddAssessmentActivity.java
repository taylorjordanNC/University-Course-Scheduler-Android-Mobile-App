package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddAssessmentActivity extends AppCompatActivity {
    SchedulerDatabase database;
    Course selectedCourse;
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
        setContentView(R.layout.activity_add_assessment);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long courseId = getIntent().getLongExtra("courseId", -1);
        selectedCourse = database.courseDao().getCourseById(courseId);

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText title = findViewById(R.id.addAssessmentTitle);
        startDate = findViewById(R.id.addAssessStartDate);
        endDate = findViewById(R.id.addAssessEndDate);

        notifyStart = findViewById(R.id.addNotifyAssessStart);
        notifyEnd = findViewById(R.id.addNotifyAssessEnd);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

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

        startDate.setOnClickListener(v -> new DatePickerDialog(AddAssessmentActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endDate.setOnClickListener(v -> new DatePickerDialog(AddAssessmentActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        FloatingActionButton returnBtn = findViewById(R.id.addAssessReturnToCourseBtn);
        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(AddAssessmentActivity.this, EditCourseActivity.class);
            returnIntent.putExtra("courseId", selectedCourse.getId());
            startActivity(returnIntent);
        });

        Spinner typeSpinner = findViewById(R.id.addAssessSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_choices,
                android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object choice = parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Random random = new Random();
        Button addAssessmentBtn = findViewById(R.id.addNewAssessmentBtn);
        addAssessmentBtn.setOnClickListener(v -> {
            Assessment newAssessment = new Assessment();

            if(!title.getText().toString().isEmpty()){
                newAssessment.setTitle(title.getText().toString());
            } else newAssessment.setTitle("New Assessment");

            if(!startDate.getText().toString().isEmpty()){
                try {
                    newAssessment.setStartDate(sdf.parse(startDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newAssessment.setStartDate(Calendar.getInstance().getTime());

            if(!endDate.getText().toString().isEmpty()){
                try {
                    newAssessment.setEndDate(sdf.parse(endDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newAssessment.setEndDate(Calendar.getInstance().getTime());

            newAssessment.setCourseId(courseId);
            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(AddAssessmentActivity.this, MyReceiver.class);
                startIntent.putExtra("toastMsg", newAssessment.getTitle() + " starting with ID: " + newAssessment.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(AddAssessmentActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                newAssessment.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(AddAssessmentActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", newAssessment.getTitle() + " ending with ID: " + newAssessment.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(AddAssessmentActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                newAssessment.setNotifyEnd(true);
            }

            if(typeSpinner.getSelectedItemPosition() == 0){
                newAssessment.setType("Performance");
            } else if(typeSpinner.getSelectedItemPosition() == 1){
                newAssessment.setType("Objective");
            }

            database.assessmentDao().addAssessment(newAssessment);
            Intent addedAssessmentIntent = new Intent(AddAssessmentActivity.this, EditCourseActivity.class);
            addedAssessmentIntent.putExtra("courseId", selectedCourse.getId());
            startActivity(addedAssessmentIntent);
        });
    }

    private void updateStartLabel(){
        startDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndLabel(){
        endDate.setText(sdf.format(endCalendar.getTime()));
    }
}