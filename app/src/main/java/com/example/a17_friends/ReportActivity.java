package com.example.a17_friends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ReportActivity extends AppCompatActivity {

    private Toolbar ReportToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        InitializeFields();
    }

    private void InitializeFields() {

        ReportToolBar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(ReportToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("回報檢舉");

    }
}
