package com.wgu.smith_taylorj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class EditAssessmentActivity extends AppCompatActivity {
    SchedulerDatabase database;
    Assessment selectedAssessment;
    CheckBox notifyStart;
    CheckBox notifyEnd;
    Calendar startCalendar;
    Calendar endCalendar;
    TextView startET;
    TextView endET;
    SimpleDateFormat sdf;
    String myFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        long assessmentId = getIntent().getLongExtra("assessmentId", -1);
        selectedAssessment = database.assessmentDao().getAssessmentById(assessmentId);

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        notifyStart = findViewById(R.id.editNotifyAssessStart);
        notifyEnd = findViewById(R.id.editNotifyAssessEnd);

        EditText titleET = findViewById(R.id.editAssessmentTitle);
        startET = findViewById(R.id.editAssessmentStart);
        endET = findViewById(R.id.editAssessmentEnd);

        titleET.setText(selectedAssessment.getTitle());
        startET.setText(sdf.format(selectedAssessment.getStartDate()));
        endET.setText(sdf.format(selectedAssessment.getEndDate()));

        FloatingActionButton saveBtn = findViewById(R.id.saveAssessmentBtn);
        FloatingActionButton returnBtn = findViewById(R.id.returnToAssessDetailsBtn);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedAssessment.getStartDate());
        endCalendar.setTime(selectedAssessment.getStartDate());
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

        startET.setOnClickListener(v -> new DatePickerDialog(EditAssessmentActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endET.setOnClickListener(v -> new DatePickerDialog(EditAssessmentActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Spinner typeSpinner = findViewById(R.id.editAssessSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_choices,
                android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        if(selectedAssessment.getType().equals("Performance")){
            typeSpinner.setSelection(0);
        } else if(selectedAssessment.getType().equals("Objective")){
            typeSpinner.setSelection(1);
        }
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
        saveBtn.setOnClickListener(v -> {
            selectedAssessment.setTitle(titleET.getText().toString());
            try {
                selectedAssessment.setStartDate(sdf.parse(startET.getText().toString()));
                selectedAssessment.setEndDate(sdf.parse(endET.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(EditAssessmentActivity.this, MyReceiver.class);
                startIntent.putExtra("toastMsg", selectedAssessment.getTitle() + " starting with ID: " + selectedAssessment.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(EditAssessmentActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                selectedAssessment.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(EditAssessmentActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", selectedAssessment.getTitle() + " ending with ID: " + selectedAssessment.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(EditAssessmentActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                selectedAssessment.setNotifyEnd(true);
            }
            if(typeSpinner.getSelectedItemPosition() == 0){
                selectedAssessment.setType("Performance");
            } else if(typeSpinner.getSelectedItemPosition() == 1){
                selectedAssessment.setType("Objective");
            }
            database.assessmentDao().updateAssessment(selectedAssessment);
            Toast toast = Toast.makeText(EditAssessmentActivity.this, "Changes Saved", Toast.LENGTH_LONG);
            toast.show();
        });

        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(EditAssessmentActivity.this, AssessmentDetailsActivity.class);
            returnIntent.putExtra("assessmentId", assessmentId);
            startActivity(returnIntent);
        });

        if(selectedAssessment.isNotifyStart()){
            notifyStart.setChecked(true);
        }
        if(selectedAssessment.isNotifyEnd()){
            notifyEnd.setChecked(true);
        }
    }

    private void updateStartLabel(){
        startET.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndLabel(){
        endET.setText(sdf.format(endCalendar.getTime()));
    }
}