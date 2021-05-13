package com.wgu.smith_taylorj;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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

public class TermDetailsActivity extends AppCompatActivity {
    long termId;
    Term selectedTerm;
    SchedulerDatabase database;
    String myFormat;
    SimpleDateFormat sdf;
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
        setContentView(R.layout.activity_term_details);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        Intent intent = getIntent();
        termId = intent.getLongExtra("termId", -1);
        selectedTerm = database.termDao().getTerm(termId);

        myFormat = "MM/dd/yyyy";
        sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);

        isNotifyStart = selectedTerm.isNotifyStart();
        isNotifyEnd = selectedTerm.isNotifyEnd();
        notifyStart = findViewById(R.id.termDetailsNotifyStart);
        notifyEnd = findViewById(R.id.termDetailsNotifyEnd);
        if(selectedTerm.isNotifyStart()){
            notifyStart.setChecked(true);
        }
        if(selectedTerm.isNotifyEnd()){
            notifyEnd.setChecked(true);
        }
        notifyStart.setClickable(false);
        notifyEnd.setClickable(false);

        TextView termTitle = findViewById(R.id.termTitle);
        TextView startDate = findViewById(R.id.termStart);
        TextView endDate = findViewById(R.id.termEnd);
        termTitle.setText(selectedTerm.getTitle());
        startDate.setText(sdf.format(selectedTerm.getStartDate()));
        endDate.setText(sdf.format(selectedTerm.getEndDate()));

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton viewAllTermsBtn = findViewById(R.id.returnToAllTermsBtn);
        viewAllTermsBtn.setOnClickListener(this::viewAllTerms);

        lv = findViewById(R.id.courseListView);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent intent1 = new Intent(getApplicationContext(), CourseDetailsActivity.class);
            long course_id;
            List<Course> courseList = database.courseDao().getCoursesByTermId(selectedTerm.getId());
            course_id = courseList.get(pos).getId();
            intent1.putExtra("courseId", course_id);
            startActivity(intent1);
        });
        updateList();

        FloatingActionButton editTermBtn = findViewById(R.id.editTermBtn);
        editTermBtn.setOnClickListener(this::openEditTerm);

        FloatingActionButton deleteTermBtn = findViewById(R.id.deleteTermBtn);
        deleteTermBtn.setOnClickListener(v -> {
            if(database.courseDao().getCoursesByTermId(selectedTerm.getId()).isEmpty()){
                new AlertDialog.Builder(TermDetailsActivity.this)
                        .setTitle("Delete Term")
                        .setMessage("Are you sure?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            database.termDao().removeTerm(selectedTerm.getId());
                            Intent deletedTerm = new Intent(TermDetailsActivity.this, TermListActivity.class);
                            startActivity(deletedTerm);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                Toast alert = Toast.makeText(getApplicationContext(), "Must remove courses before deleting term.", Toast.LENGTH_LONG);
                alert.show();
            }
        });

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(selectedTerm.getStartDate());
        endCalendar.setTime(selectedTerm.getEndDate());
    }

    private void viewAllTerms(View view){
        Intent intent = new Intent(TermDetailsActivity.this, TermListActivity.class);
        startActivity(intent);
    }

    private void openEditTerm(View view){
        Intent intent = new Intent(TermDetailsActivity.this, EditTermActivity.class);
        intent.putExtra("termId", selectedTerm.getId());
        startActivity(intent);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_termdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Random random = new Random();
        int id = item.getItemId();
        switch(id){
            case R.id.notifyAtStart:
                if(!isNotifyStart){
                    Intent startIntent = new Intent(TermDetailsActivity.this, MyReceiver.class);
                    startIntent.putExtra("toastMsg", selectedTerm.getTitle() + " starting with ID: " + selectedTerm.getId());
                    PendingIntent senderStart = PendingIntent.getBroadcast(TermDetailsActivity.this, random.nextInt(), startIntent, 0);
                    AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    startDate = startCalendar.getTimeInMillis();
                    alarmManagerStart.set(AlarmManager.RTC_WAKEUP, startDate, senderStart);
                    selectedTerm.setNotifyStart(true);
                    database.termDao().updateTerm(selectedTerm);
                    notifyStart.setChecked(true);
                    return true;
                } else {
                    Toast alertIsNotifying = Toast.makeText(getApplicationContext(), "Notification for start already set", Toast.LENGTH_LONG);
                    alertIsNotifying.show();
                }
            case R.id.notifyAtEnd:
                if (!isNotifyEnd){
                    Intent endIntent = new Intent(TermDetailsActivity.this, MyReceiver.class);
                    endIntent.putExtra("toastMsg", selectedTerm.getTitle() + " ending with ID: " + selectedTerm.getId());
                    PendingIntent senderEnd = PendingIntent.getBroadcast(TermDetailsActivity.this, random.nextInt(), endIntent, 0);
                    AlarmManager alarmManagerEnd = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    endDate = endCalendar.getTimeInMillis();
                    alarmManagerEnd.set(AlarmManager.RTC_WAKEUP, endDate, senderEnd);
                    selectedTerm.setNotifyEnd(true);
                    database.termDao().updateTerm(selectedTerm);
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