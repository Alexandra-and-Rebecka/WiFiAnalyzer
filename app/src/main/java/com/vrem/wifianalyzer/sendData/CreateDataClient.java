package com.vrem.wifianalyzer.sendData;

import android.content.Context;
import android.os.AsyncTask;

import com.vrem.wifianalyzer.authentication.Client;

public class CreateDataClient {

    private String username;
    private String password;
    private Context context;

    public CreateDataClient(String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.context = context;
    }

    public void execute() {
        new DataClient("10.0.2.2", 5050, username, password, context);
    }
}