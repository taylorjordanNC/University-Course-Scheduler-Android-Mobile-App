package com.wgu.smith_taylorj;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class AssessmentListActivity extends AppCompatActivity{
    private SchedulerDatabase database;
    private ListView lv;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

//        lv = findViewById(R.id.assessmentsListView);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent intent = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
            long assessment_id;
            List<Assessment> assessmentList = database.assessmentDao().getAllAssessments();
            assessment_id = assessmentList.get(pos).getId();
            intent.putExtra("assessmentId", assessment_id);
            startActivity(intent);
        });
        updateList();

        Button btn = findViewById(R.id.addNewAssessmentBtn);
        btn.setOnClickListener(this::addNewAssessment);

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addNewAssessment(View view){
        Intent intent = new Intent(this, AddAssessmentActivity.class);
        startActivity(intent);
    }

    private void updateList(){
        List<Assessment> allAssessments = database.assessmentDao().getAllAssessments();
        String[] items = new String[allAssessments.size()];
        if(!allAssessments.isEmpty()){
            for(int i = 0; i < allAssessments.size(); i++){
                items[i] = allAssessments.get(i).getTitle();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}