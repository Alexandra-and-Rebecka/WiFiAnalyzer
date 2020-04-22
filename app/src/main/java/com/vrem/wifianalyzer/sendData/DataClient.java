package com.vrem.wifianalyzer.sendData;

import android.content.Context;

import androidx.core.util.Pair;

import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.TorConfig;
import com.msopentech.thali.toronionproxy.TorInstaller;
import com.msopentech.thali.toronionproxy.Utilities;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.tor.TorResourceInstaller;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Policy;
import java.security.Security;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class DataClient {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private List<WiFiDetail> wiFiDetails = null;

    public DataClient(String address, int port, List<WiFiDetail> wifiDetails, String latitude, String longitude, Context context) {

        this.wiFiDetails = wifiDetails;
        try {
            // configuration files
            int STARTUP_TIMEOUT_SEC = 30;
            int STARTUP_TRIES = 2;
            int PORT = 10462;

            File nativeDir = new File(context.getApplicationInfo().nativeLibraryDir);
            File configDir = new File(context.getFilesDir(), "tor-config");
            if (!configDir.exists()) {
                configDir.mkdir();
            }
            TorConfig torConfig = new TorConfig.Builder(nativeDir, configDir).fileCreationTimeout(STARTUP_TIMEOUT_SEC).build();
            TorInstaller torInstaller = new TorInstaller() {

                protected TorResourceInstaller resourceInstaller = new TorResourceInstaller(context, configDir);
                @Override
                public void setup() throws IOException {
                    resourceInstaller.installResources();
                }

                @Override
                public void updateTorConfigCustom(String content) throws IOException {
                    File f = resourceInstaller.getTorrcFile();
                    if(f != null) {
                        resourceInstaller.updateTorConfigCustom(f, content);
                    }

                }

                @Override
                public InputStream openBridgesStream() {
                    return context.getResources().openRawResource(R.raw.bridges);
                }
            };

            // setup manager
            AndroidOnionProxyManager onionProxyManager = new AndroidOnionProxyManager(context, torConfig, torInstaller, null, null, null);
            try {
                onionProxyManager.setup();
                onionProxyManager.getTorInstaller().updateTorConfigCustom("ControlPort auto" +
                        "\nControlPortWriteToFile " + onionProxyManager.getContext().getConfig().getControlPortFile() +
                        "\nCookieAuthFile " + onionProxyManager.getContext().getConfig().getCookieAuthFile() +
                        "\nCookieAuthentication 1" +
                        "\nSocksPort " + PORT);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (TimeoutException e) {
                System.out.println(e.getMessage());
            }

            // start tor
            if (!onionProxyManager.startWithRepeat(STARTUP_TIMEOUT_SEC, STARTUP_TRIES, true)) {
                System.out.println("could not start TOR after $STARTUP_TRIES with ${STARTUP_TIMEOUT_SEC}s timeout");
            } else {
                System.out.println("successfully started TOR");



                while (!onionProxyManager.isRunning())
                    Thread.sleep(90);
                System.out.println("Tor initialized on port " + onionProxyManager.getIPv4LocalHostSocksPort());

                Socket socket =
                        Utilities.socks4aSocketConnection(address, port, "127.0.0.1", PORT );
                System.out.println("Connected");

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                System.out.println("Sending Data");

                //TODO send certificate and token

                for(WiFiDetail wiFiDetail : wiFiDetails) {
                    out.writeUTF(wiFiDetail.getBSSID());
                    out.writeUTF(wiFiDetail.getSSID());
                    out.writeUTF(String.valueOf(wiFiDetail.getWiFiSignal().getLevel()));
                    out.writeUTF(wiFiDetail.getWiFiSignal().getDistance());
                    //out.writeUTF(wiFiDetail.getTitle());
                    out.writeUTF(latitude);
                    out.writeUTF(longitude);
                }

                out.writeUTF("done");

                System.out.println(in.readUTF());

                in.close();
                out.close();
                socket.close();

                System.out.println("Connection Closed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
