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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);

        final EditText username = (EditText) findViewById(R.id.usernameRegister);
        final EditText password = (EditText) findViewById(R.id.passwordRegister);
        Button registerButton = (Button) findViewById(R.id.Register);
        TextView loginButton = (TextView) findViewById(R.id.loginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClient client = new CreateClient(username.getText().toString(), password.getText().toString(), "register",getApplicationContext());
                client.execute();
                while(client.getResult().equals("")) {
                }
                if(client.getResult().equals("Success")){
                    Intent myIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    RegisterActivity.this.startActivity(myIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), client.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, AuthenticationActivity.class);
                RegisterActivity.this.startActivity(myIntent);
                finish();
            }
        });

    }
}