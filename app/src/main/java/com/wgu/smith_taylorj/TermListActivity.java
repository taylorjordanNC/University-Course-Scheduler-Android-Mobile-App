package com.wgu.smith_taylorj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;

import androidx.loader.content.CursorLoader;
import android.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TermListActivity extends AppCompatActivity{
    private ListView lv;
    private SchedulerDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        database = SchedulerDatabase.getDatabase(getApplicationContext());

        lv = findViewById(R.id.termsListView);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);
            long term_id;
            List<Term> termList = database.termDao().getAllTerms();
            term_id = termList.get(pos).getId();
            intent.putExtra("termId", term_id);
            startActivity(intent);
        });
        updateList();

        FloatingActionButton btn = findViewById(R.id.addNewTermBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTerm(v);
            }
        });

        getSupportActionBar().setTitle("Student Schedule App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewTerm(View view){
        Intent intent = new Intent(this, AddTermActivity.class);
        startActivity(intent);
    }

    private void updateList(){
        List<Term> allTerms = database.termDao().getAllTerms();
        String[] items = new String[allTerms.size()];
        if(!allTerms.isEmpty()){
            for(int i = 0; i < allTerms.size(); i++){
                items[i] = allTerms.get(i).getTitle();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}