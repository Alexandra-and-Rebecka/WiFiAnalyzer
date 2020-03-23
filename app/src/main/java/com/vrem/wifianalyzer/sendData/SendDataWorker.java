package com.vrem.wifianalyzer.sendData;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

        new CreateDataClient("Test1", "Test2", mContext).execute();
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}
