package com.vrem.wifianalyzer.sendData;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.band.WiFiChannels;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.predicate.FilterPredicate;
import com.vrem.wifianalyzer.wifi.scanner.*;

import org.apache.commons.collections4.Predicate;

import java.util.List;

public class SendDataWorker extends Worker {

    private Context mContext;
    private String lon;
    private String lat;

    public SendDataWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, send WiFi data.


        List<WiFiDetail> wiFiDetails = MainContext.INSTANCE.getScannerService().getWiFiData().getWiFiDetails();

        for(WiFiDetail wiFiDetail : wiFiDetails) {
            System.out.println("----------------------");
            System.out.println(wiFiDetail.getBSSID());
            System.out.println(wiFiDetail.getSSID());
            System.out.println(wiFiDetail.getWiFiSignal());
            System.out.println(wiFiDetail.getTitle());
        }
        System.out.println("----------------------");

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData(mFusedLocationClient);
                            } else {
                                lat = String.valueOf(location.getLatitude());
                                lon = String.valueOf(location.getLongitude());
                            }
                        }
                    }
            );
        } else {
            Toast.makeText(mContext, "Turn on location", Toast.LENGTH_LONG).show();
        }

        while(lat == null || lon == null );


        if (lat != null && lon != null ){

        new CreateDataClient(wiFiDetails, lat, lon, mContext).execute();
        // Indicate whether the task finished successfully with the Result
        return Result.success();
        } else {
            return Result.failure();
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestNewLocationData(FusedLocationProviderClient mFusedLocationClient){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    };
}
