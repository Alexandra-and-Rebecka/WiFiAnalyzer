package com.vrem.wifianalyzer.tor;

import android.content.Context;
import android.os.AsyncTask;

import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.TorConfig;
import com.msopentech.thali.toronionproxy.TorInstaller;
import com.msopentech.thali.toronionproxy.Utilities;
import com.vrem.wifianalyzer.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

public class Tor extends AsyncTask<Void, Void, Void> {

    private Context context;
    public Tor(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {

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
                        "\nSocksPort 10462");

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

                int hiddenServicePort = 5050;
                int localPort = 10462;


                while (!onionProxyManager.isRunning())
                    Thread.sleep(90);
                System.out.println("Tor initialized on port " + onionProxyManager.getIPv4LocalHostSocksPort());

                Socket socket =
                        Utilities.socks4aSocketConnection("80.216.31.62", hiddenServicePort, "127.0.0.1", localPort );
                System.out.println("Connected");
                System.out.println(socket);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("Sending Data");

                out.writeUTF("TEST");
                out.writeUTF("TEST");

                System.out.println(in.readUTF());
                System.out.println(in.readUTF());

                in.close();
                out.close();
                socket.close();

                System.out.println("Connection Closed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }



        //Intent intent = new Intent(this, AuthenticationActivity.class);
        //startActivity(intent);
        //finish();
        return null;
    }

}
