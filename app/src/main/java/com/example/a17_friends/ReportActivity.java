package com.example.a17_friends;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    private Button SendReport;
    private EditText ReportTitle,ReportMessage;
    private Toolbar ReportToolBar;
    private  String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        SendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GetReportTitle = ReportTitle.getText().toString();
                String GetReportMessage = ReportMessage.getText().toString();
                String currentDate,currentTime;

                if (TextUtils.isEmpty(GetReportTitle))
                {
                    Toast.makeText(ReportActivity.this, "請輸入標題...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(GetReportMessage))
                {
                    Toast.makeText(ReportActivity.this, "請輸入內容...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyy MMM dd");
                    currentDate = currentDateFormat.format(calForDate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss a");
                    currentTime = currentTimeFormat.format(calForTime.getTime());

                    HashMap<String,Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("uid",currentUserID);
                    messageInfoMap.put("Title",GetReportTitle);
                    messageInfoMap.put("Message",GetReportMessage);
                    messageInfoMap.put("date",currentDate);
                    messageInfoMap.put("time",currentTime);
                    RootRef.child("Report").child(currentDate+","+currentTime).updateChildren(messageInfoMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ReportActivity.this, "回報成功!!", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String message = task.getException().toString();
                                        Toast.makeText(ReportActivity.this, "錯誤 : " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void InitializeFields() {

        ReportToolBar = (Toolbar) findViewById(R.id.report_toolbar);
        ReportTitle = (EditText) findViewById(R.id.ReportTitle);
        ReportMessage = (EditText) findViewById(R.id.ReportMessage);
        SendReport = (Button) findViewById(R.id.SendReport);
        setSupportActionBar(ReportToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("回報檢舉");

    }


}
