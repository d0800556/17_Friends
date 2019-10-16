package com.example.a17_friends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MoreSettingsActivity extends AppCompatActivity {

    private Toolbar SettingsToolBar;
    private Button change_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_settings);

        InitializeFields();

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                changepassword();
            }
        });
    }

    private void InitializeFields() {

        change_button = (Button)findViewById(R.id.change_button);

        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("更多設定");

    }

    private void changepassword() {  //修改密碼區
        AlertDialog dialog =null;
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("修改密碼");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt= new EditText(this);
        emailEt.setHint("輸入Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(20);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(20,20,20,20);
        builder.setView(linearLayout);
        builder.setPositiveButton("送出", new DialogInterface.OnClickListener() {  //設定送出按鈕
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email錯誤或不存在", Toast.LENGTH_SHORT).show();
                } else{


                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final ProgressDialog progressDialog = new ProgressDialog(MoreSettingsActivity.this);
                    progressDialog.setMessage("驗證中..");
                    progressDialog.show();

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "密碼重設已寄出",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                "Email 不存在", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //設定取消按鈕
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog =builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

}
