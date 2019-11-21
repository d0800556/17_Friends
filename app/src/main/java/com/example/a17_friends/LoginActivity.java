package com.example.a17_friends;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.sax.TextElementListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseIndexArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private FirebaseUser currentUser;
    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;
    private DatabaseReference RootRef;

    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToRegisterActivity();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowUserToLogin();
            }
        });



        ForgetPasswordLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                forgetpassword();
                }


        });

    }


    private void forgetpassword() {  //找回密碼區
        AlertDialog dialog =null;
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("忘記密碼");
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
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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

    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "請輸入信箱...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "請輸入密碼...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("登入中");
            loadingBar.setMessage("請稍後~~");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                UsersRef.child(currentUserId).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task)
                                                                   {
                                                                       if (task.isSuccessful())
                                                                       {
                                                                           SendUserToMainActivity();
                                                                           loadingBar.dismiss();
                                                                       }
                                                                   }
                                                               });
                                                }

                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "錯誤 : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);


    }

    protected  void onStart()
    {
        super.onStart();
        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void InitializeFields() {
        LoginButton =  (Button) findViewById(R.id.login_button);

        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText)findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView)findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = (TextView)findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

    }
    }
