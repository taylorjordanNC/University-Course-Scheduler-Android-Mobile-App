package com.wgu.smith_taylorj;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AssessmentDetailsActivity extends AppCompatActivity {
    Assessment selectedAssessment;
    SchedulerDatabase database;
    Calendar startCalendar;
    Calendar endCalendar;
    long startDate;
    long endDate;
    boolean isNotifyStart;
    boolean isNotifyEnd;
    CheckBox notifyStart;
    CheckBox notifyEnd;
    String myFormat;
    SimpleDateFormat sdf;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long assessmentId = getIntent().getLongExtra("assessmentId", -1);
        selectedAssessment = database.assessmentDao().getAssessmentById(assessmentId);

        notifyStart = findViewById(R.id.assessDetailsNotifyStart);
        notifyEnd = findViewById(R.id.assessDetailsNotifyEnd);
        isNotifyStart = selectedAssessment.isNotifyStart();
        isNotifyEnd = selectedAssessment.isNotifyEnd();
        notifyStart.setChecked(isNotifyStart);
        notifyEnd.setChecked(isNotifyEnd);
        notifyStart.setClickable(false);
        notifyEnd.setClickable(false);

        TextView assessmentTitle = findViewById(R.id.assessmentTitle);
        TextView startDate = findViewById(R.id.assessmentStart);
        TextView endDate = findViewById(R.id.assessmentEnd);

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        assessmentTitle.setText(selectedAssessment.getTitle());
        startDate.setText(sdf.format(selectedAssessment.getStartDate()));
        endDate.setText(sdf.format(selectedAssessment.getEndDate()));

        FloatingActionButton editBtn = findViewById(R.id.editAssessmentBtn);
        FloatingActionButton deleteBtn = findViewById(R.id.deleteAssessmentBtn);
        FloatingActionButton returnBtn = findViewById(R.id.returnToCourseDetailsBtn);

        editBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(AssessmentDetailsActivity.this, EditAssessmentActivity.class);
            editIntent.putExtra("assessmentId", assessmentId);
            startActivity(editIntent);
        });

        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(AssessmentDetailsActivity.this)
                    .setTitle("Delete Assessment")
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Intent deleteIntent = new Intent(AssessmentDetailsActivity.this, CourseDetailsActivity.class);
                        deleteIntent.putExtra("courseId", selectedAssessment.getCourseId());
                        database.assessmentDao().removeAssessment(assessmentId);
                        startActivity(deleteIntent);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(AssessmentDetailsActivity.this, CourseDetailsActivity.class);
            returnIntent.putExtra("courseId", selectedAssessment.getCourseId());
            startActivity(returnIntent);
        });

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedAssessment.getStartDate());
        endCalendar.setTime(selectedAssessment.getEndDate());

        Spinner typeSpinner = findViewById(R.id.assessDetailsSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_choices,
                android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        if(selectedAssessment.getType().equals("Performance")){
            typeSpinner.setSelection(0);
        } else if(selectedAssessment.getType().equals("Objective")){
            typeSpinner.setSelection(1);
        }
        typeSpinner.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_assessmentdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Random random = new Random();
        int id = item.getItemId();
        switch(id){
            case R.id.notifyAtStart:
                if(!isNotifyStart){
                    Intent startIntent = new Intent(AssessmentDetailsActivity.this, MyReceiver.class);
                    startIntent.putExtra("toastMsg", selectedAssessment.getTitle() + " starting with ID: " + selectedAssessment.getId());
                    PendingIntent senderStart = PendingIntent.getBroadcast(AssessmentDetailsActivity.this, random.nextInt(), startIntent, 0);
                    AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    startDate = startCalendar.getTimeInMillis();
                    alarmManagerStart.set(AlarmManager.RTC_WAKEUP, startDate, senderStart);
                    selectedAssessment.setNotifyStart(true);
                    database.assessmentDao().updateAssessment(selectedAssessment);
                    notifyStart.setChecked(true);
                    return true;
                } else {
                    Toast alertIsNotifying = Toast.makeText(getApplicationContext(), "Notification for start already set", Toast.LENGTH_LONG);
                    alertIsNotifying.show();
                }

            case R.id.notifyAtEnd:
                if(!isNotifyEnd){
                    Intent endIntent = new Intent(AssessmentDetailsActivity.this, MyReceiver.class);
                    endIntent.putExtra("toastMsg", selectedAssessment.getTitle() + " ending with ID: " + selectedAssessment.getId());
                    PendingIntent senderEnd = PendingIntent.getBroadcast(AssessmentDetailsActivity.this, random.nextInt(), endIntent, 0);
                    AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    endDate = endCalendar.getTimeInMillis();
                    alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, endDate, senderEnd);
                    selectedAssessment.setNotifyEnd(true);
                    database.assessmentDao().updateAssessment(selectedAssessment);
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