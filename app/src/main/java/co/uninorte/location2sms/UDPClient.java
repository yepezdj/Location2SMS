package co.uninorte.location2sms;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@SuppressWarnings("deprecation")
public class UDPClient  extends AsyncTask<String,Void,Void> {

    @Override
    protected Void doInBackground(String... voids) {

        try{
            String message = voids[0];

            InetAddress ip=InetAddress.getByName("192.168.1.5");
            DatagramSocket socket=new DatagramSocket();
            byte[] outData = (message).getBytes();

            DatagramPacket out = new DatagramPacket(outData,outData.length,ip ,1010);
            socket.send(out);
            //Toast.makeText( UDPClient, "Send >>> ", Toast.LENGTH_SHORT).show();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        }
        return null;

    }


}
