package com.vrem.wifianalyzer.sendData;

import android.content.Context;
import android.os.AsyncTask;

import com.vrem.wifianalyzer.authentication.Client;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;

import java.util.List;

public class CreateDataClient {

    private List<WiFiDetail> wiFiDetails;
    private String latitude;
    private String longitude;
    private Context context;

    public CreateDataClient(List<WiFiDetail> wiFiDetails, String latitude, String longitude, Context context) {
        this.wiFiDetails = wiFiDetails;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void execute() {
        new DataClient("80.216.31.62", 5050, wiFiDetails, latitude, longitude, context);
    }
}