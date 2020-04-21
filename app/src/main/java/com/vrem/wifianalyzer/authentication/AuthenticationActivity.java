package com.vrem.wifianalyzer.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_view);

        final EditText username = (EditText) findViewById(R.id.usernameLogin);
        final EditText password = (EditText) findViewById(R.id.passwordLogin);
        Button loginButton = (Button) findViewById(R.id.Login);
        TextView registerButton = (TextView) findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClient client = new CreateClient(username.getText().toString(), password.getText().toString(), "login", getApplicationContext());
                client.execute();
                while(client.getResult().equals("")) {
                }
                if(client.getResult().equals("Success")){
                    Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    AuthenticationActivity.this.startActivity(myIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), client.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
                AuthenticationActivity.this.startActivity(myIntent);
                finish();
            }
        });

    }
}