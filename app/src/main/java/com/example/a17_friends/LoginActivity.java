package com.example.a17_friends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    protected  void onStart()
    {
        super.onStart();
        if (currentUser = null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {

        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.this)
                startActivity(loginIntent)
    }
}
