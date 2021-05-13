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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddTermActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_term);
        database = SchedulerDatabase.getDatabase(getApplicationContext());
        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText title = findViewById(R.id.addTermTitle);
        startDate = findViewById(R.id.addTermStart);
        endDate = findViewById(R.id.addTermEnd);

        notifyStart = findViewById(R.id.addTermNotifyStart);
        notifyEnd = findViewById(R.id.addTermNotifyEnd);

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
        DatePickerDialog.OnDateSetListener myEndDate = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndLabel();
            }
        };

        startDate.setOnClickListener(v -> new DatePickerDialog(AddTermActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endDate.setOnClickListener(v -> new DatePickerDialog(AddTermActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Random random = new Random();
        Button addNewTermBtn = findViewById(R.id.addNewTermBtn);
        addNewTermBtn.setOnClickListener(v -> {
            Term newTerm = new Term();
            if(!title.getText().toString().isEmpty()){
                newTerm.setTitle(title.getText().toString());
            } else newTerm.setTitle("New Term");

            if(!startDate.getText().toString().isEmpty()){
                try {
                    newTerm.setStartDate(sdf.parse(startDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newTerm.setStartDate(Calendar.getInstance().getTime());

            if(!endDate.getText().toString().isEmpty()) {
                try {
                    newTerm.setEndDate(sdf.parse(endDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else newTerm.setEndDate(Calendar.getInstance().getTime());

            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(AddTermActivity.this, MyReceiver.class);
                startIntent.putExtra("toastMsg", newTerm.getTitle() + " starting with ID: " + newTerm.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(AddTermActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                newTerm.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(AddTermActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", newTerm.getTitle() + " ending with ID: " + newTerm.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(AddTermActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                newTerm.setNotifyEnd(true);
            }
            database.termDao().addTerm(newTerm);
            Intent addedTermIntent = new Intent(AddTermActivity.this, TermListActivity.class);
            startActivity(addedTermIntent);
        });

        FloatingActionButton returnBtn = findViewById(R.id.returnToTermsBtn);
        returnBtn.setOnClickListener(v -> {
            Intent returnToTermsIntent = new Intent(AddTermActivity.this, TermListActivity.class);
            startActivity(returnToTermsIntent);
        });
    }

    private void updateStartLabel(){
        startDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndLabel(){
        endDate.setText(sdf.format(endCalendar.getTime()));
    }
}