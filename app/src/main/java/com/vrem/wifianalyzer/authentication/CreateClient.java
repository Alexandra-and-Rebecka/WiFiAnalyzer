package com.vrem.wifianalyzer.authentication;

import android.content.Context;
import android.os.AsyncTask;

public class CreateClient extends AsyncTask<Void, Void, Void> {

    private String username;
    private String password;
    private Context context;

    public CreateClient(String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.context = context;
    }

    @Override
    public Void doInBackground(Void... voids) {
        new Client("10.0.2.2", 5000, username, password, context);
        return null;
    }
}