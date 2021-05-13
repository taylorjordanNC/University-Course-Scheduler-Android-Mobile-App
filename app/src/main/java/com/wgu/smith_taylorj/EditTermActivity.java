package com.wgu.smith_taylorj;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EditTermActivity extends AppCompatActivity {
    Term selectedTerm;
    long termId;
    SchedulerDatabase database;
    ListView lv;
    Calendar startCalendar;
    Calendar endCalendar;
    TextView startDate;
    TextView endDate;
    CheckBox notifyStart;
    CheckBox notifyEnd;
    String myFormat;
    SimpleDateFormat sdf;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId", -1);
        selectedTerm = database.termDao().getTerm(termId);

        lv = findViewById(R.id.editTermListView);

        myFormat = "MM/dd/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        notifyStart = findViewById(R.id.editTermNotifyStart);
        notifyEnd = findViewById(R.id.editTermNotifyEnd);

        Button addCourseBtn = findViewById(R.id.addNewCourseBtn);
        addCourseBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(EditTermActivity.this, AddCourseActivity.class);
            intent1.putExtra("termId", selectedTerm.getId());
            startActivity(intent1);
        });

        FloatingActionButton returnToDetailsBtn = findViewById(R.id.returnToDetailsBtn);
        returnToDetailsBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(EditTermActivity.this, TermDetailsActivity.class);
            intent2.putExtra("termId", termId);
            startActivity(intent2);
        });

        FloatingActionButton saveChangesBtn = findViewById(R.id.saveTermBtn);
        EditText title = findViewById(R.id.editTermTitle);
        startDate = findViewById(R.id.editTermStart);
        endDate = findViewById(R.id.editTermEnd);
        title.setText(selectedTerm.getTitle());
        startDate.setText(sdf.format(selectedTerm.getStartDate()));
        endDate.setText(sdf.format(selectedTerm.getEndDate()));

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedTerm.getStartDate());
        endCalendar.setTime(selectedTerm.getEndDate());
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

        startDate.setOnClickListener(v -> new DatePickerDialog(EditTermActivity.this, myStartDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());
        endDate.setOnClickListener(v -> new DatePickerDialog(EditTermActivity.this, myEndDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Random random = new Random();
        saveChangesBtn.setOnClickListener(v -> {
            selectedTerm.setTitle(title.getText().toString());
            try {
                selectedTerm.setStartDate(sdf.parse(startDate.getText().toString()));
                selectedTerm.setEndDate(sdf.parse(endDate.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(notifyStart.isChecked()){
                Intent startIntent = new Intent(EditTermActivity.this, MyReceiver.class);
                startIntent.putExtra("toastMsg", selectedTerm.getTitle() + " starting with ID: " + selectedTerm.getId());
                PendingIntent senderStart = PendingIntent.getBroadcast(EditTermActivity.this, random.nextInt(), startIntent, 0);
                AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long start = startCalendar.getTimeInMillis();
                alarmManagerStart.set(AlarmManager.RTC_WAKEUP, start, senderStart);
                selectedTerm.setNotifyStart(true);
            }
            if(notifyEnd.isChecked()){
                Intent endIntent = new Intent(EditTermActivity.this, MyReceiver.class);
                endIntent.putExtra("toastMsg", selectedTerm.getTitle() + " ending with ID: " + selectedTerm.getId());
                PendingIntent senderEnd = PendingIntent.getBroadcast(EditTermActivity.this, random.nextInt(), endIntent, 0);
                AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long end = endCalendar.getTimeInMillis();
                alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, end, senderEnd);
                selectedTerm.setNotifyEnd(true);
            }
            database.termDao().updateTerm(selectedTerm);
            Toast alert = Toast.makeText(EditTermActivity.this, "Changes Saved", Toast.LENGTH_LONG);
            alert.show();
        });

        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent intent1 = new Intent(EditTermActivity.this, CourseDetailsActivity.class);
            long course_id;
            List<Course> courseList = database.courseDao().getCoursesByTermId(selectedTerm.getId());
            course_id = courseList.get(pos).getId();
            intent1.putExtra("courseId", course_id);
            startActivity(intent1);
        });
        updateList();

        if(selectedTerm.isNotifyStart()){
            notifyStart.setChecked(true);
        }
        if(selectedTerm.isNotifyEnd()){
            notifyEnd.setChecked(true);
        }
    }

    private void updateList(){
        List<Course> termCourses = database.courseDao().getCoursesByTermId(termId);
        String[] items = new String[termCourses.size()];
        if(!termCourses.isEmpty()){
            for(int i = 0; i < termCourses.size(); i++){
                items[i] = termCourses.get(i).getTitle();
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