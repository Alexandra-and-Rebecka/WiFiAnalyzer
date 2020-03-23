package com.vrem.wifianalyzer.sendData;

import android.content.Context;

import androidx.core.util.Pair;

import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;

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
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class DataClient {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public DataClient(String address, int port, String username, String password, Context context) {
        try {

            Security.addProvider(new BouncyCastleProvider());

            KeyStore truststore = KeyStore.getInstance("PKCS12", "BC");
            KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
            char[] truststorePassword = "AlexReb123!".toCharArray();

            String tspath = context.getFilesDir() + "/" + "clientTest.truststore";
            String kspath = context.getFilesDir() + "/" + "clientTest.keystore";

            InputStream trustStoreData = context.getAssets().open("client.truststore");
            truststore.load(trustStoreData, truststorePassword);

            InputStream keyStoreData = context.getAssets().open("client.keystore");
            keystore.load(keyStoreData, truststorePassword);


            String trustMgrFactAlg = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance(trustMgrFactAlg);
            trustMgrFact.init(truststore);

            String keystoreMgrAlg = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory keyMgrFact = KeyManagerFactory.getInstance(keystoreMgrAlg);
            keyMgrFact.init(keystore, truststorePassword);

            SSLContext clientContext = SSLContext.getInstance("TLSv1");
            clientContext.init(keyMgrFact.getKeyManagers(), trustMgrFact.getTrustManagers(), null);

            SSLSocketFactory fact = clientContext.getSocketFactory();
            socket = (SSLSocket) fact.createSocket(address, port);
            System.out.println("Connected");

            in = new DataInputStream(socket.getInputStream());
            InputStream inputStream = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Sending Data");

            out.writeUTF(username);
            out.writeUTF(password);

            System.out.println(in.readUTF());
            System.out.println(in.readUTF());

            in.close();
            out.close();
            socket.close();

            System.out.println("Connection Closed");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
