package com.vrem.wifianalyzer.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_view);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        Button registerButton = (Button) findViewById(R.id.Register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateClient(username.getText().toString(), password.getText().toString(), getApplicationContext()).execute();
                Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                AuthenticationActivity.this.startActivity(myIntent);
                finish();
            }
        });

    }
}