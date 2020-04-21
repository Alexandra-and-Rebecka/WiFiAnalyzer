package com.vrem.wifianalyzer.authentication;

import android.content.Context;
import android.content.Intent;

import com.vrem.wifianalyzer.MainActivity;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
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


public class Client {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private String result = "";

    public Client(String address, int port, String username, String password, String type, Context context) {
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
            socket = (SSLSocket) fact.createSocket(address, port) ;
            System.out.println("Connected");

            in = new DataInputStream(socket.getInputStream());
            InputStream inputStream = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(type); //register or login

            out.writeUTF(username);
            out.writeUTF(password);

            this.result = in.readUTF();
            System.out.println(result);
            if(!result.equals("Success")){

                in.close();
                out.close();
                socket.close();

                System.out.println("Connection Closed");
                return;
            }
            System.out.println(in.readUTF());

            if(type.equals("register")) {

                byte[] buff = new byte[8000];
                int bytesRead = (inputStream.read(buff));
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bao.write(buff, 0, bytesRead);

                byte[] data = bao.toByteArray();
                writeFileOnInternalStorage(tspath, data);

                System.out.println("Okay");
                out.writeUTF("Trustore Received");

                buff = new byte[8000];
                bytesRead = (inputStream.read(buff));;
                bao = new ByteArrayOutputStream();
                    bao.write(buff, 0, bytesRead);

                data = bao.toByteArray();
                writeFileOnInternalStorage(kspath, data);

                System.out.println("Okay");
                out.writeUTF("Keystore Received");

                //String received = in.readUTF();
                //System.out.println(received);

                //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("clientTP.keystore", Context.MODE_PRIVATE));
                //outputStreamWriter.write(received);
                //outputStreamWriter.close();
            }

            in.close();
            out.close();
            socket.close();

            System.out.println("Connection Closed");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getResult() {
        return this.result;
    }

    public void writeFileOnInternalStorage(String path, byte[] data) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }

        try{
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(data);
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }

    }
}
