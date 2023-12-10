package com.cs407.closetcalendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.credentials.GetCredentialRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;

public class LoginActivity extends AppCompatActivity {

    // future reference: https://developer.android.com/training/sign-in/credential-manager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        Intent intent = new Intent(this, CalendarMainActivity.class);
        startActivity(intent);
    }
}