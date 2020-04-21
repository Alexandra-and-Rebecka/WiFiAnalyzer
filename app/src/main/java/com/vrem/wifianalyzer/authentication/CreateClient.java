package com.vrem.wifianalyzer.authentication;

import android.content.Context;
import android.os.AsyncTask;

public class CreateClient extends AsyncTask<Void, Void, Void> {

    private String username;
    private String password;
    private String type;
    private String res = "";
    private Context context;

    public CreateClient(String username, String password, String type,Context context) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.context = context;
    }

    @Override
    public Void doInBackground(Void... voids) {
        Client client = new Client("10.0.2.2", 5000, username, password, type, context);
        res = client.getResult();
        return null;
    }

    public String getResult() {
        return this.res;
    }
}