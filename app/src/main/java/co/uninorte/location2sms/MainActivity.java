package co.uninorte.location2sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //Initialize variable
    TextView textView1, textView2, textView3;
    FusedLocationProviderClient fusedLocationProviderClient;

    String txtMessage;
    //Direcciones Ip's de las instancias de AWS


    //String ip1 = "3.215.220.179";
    //String ip2 = "54.227.210.76";
    //String ip3;
    @SuppressWarnings("deprecation")
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assing variable

        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);

        //Initialize focusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
        } else {
            //When permission denied
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        @SuppressLint("SetTextI18n")
        public void onLocationChanged(Location location) {
            //Set latitude on TextView
            textView1.setText(Html.fromHtml(
                    "<font color= '#6200EE'><b>Latitud :</b><br></font>"
                            + location.getLatitude()
            ));
            //Set longitude on TextView
            textView2.setText(Html.fromHtml(
                    "<font color= '#6200EE'><b>Longitud :</b><br></font>"
                            + location.getLongitude()
            ));
            //Set time stamp
            textView3.setText(Html.fromHtml(
                    "<font color= '#6200EE'><b>TimeStamp :</b><br></font>"
                            + new Date().toString()));
            //Set message
            txtMessage = (location.getLatitude()+","+location.getLongitude()+","+new Date().toString());
        }
    }

    public void stopRepeating(View v) {
        mHandler.removeCallbacks(EnvioRunnable);
    }

    public void startRepeating(View v) {
        //mHandler.postDelayed(mToastRunnable, 5000);
        EnvioRunnable.run();
    }



    private final Runnable EnvioRunnable = new Runnable() {
        @Override
        public void run() {
            enviarr();
            Toast.makeText(getApplicationContext(), "Sent UDP message", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, 5000);
        }
    };

    
    public void enviarr() {
        //noinspection deprecation
        new UDPClient().execute(txtMessage);
    }



    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class UDPClient extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {
                String message = voids[0];

                //InetAddress ip_1 = InetAddress.getByName(ip1);
                InetAddress ip = InetAddress.getByName("54.227.210.76");
                //InetAddress ip_3 = InetAddress.getByName(ip3);
                DatagramSocket socket = new DatagramSocket();
                byte[] outData = (message).getBytes();

                //DatagramPacket out_1 = new DatagramPacket(outData, outData.length, ip_1, 11000);
                //socket.send(out_1);
                DatagramPacket out = new DatagramPacket(outData, outData.length, ip, 11000);
                socket.send(out);
                //DatagramPacket out_3 = new DatagramPacket(outData, outData.length, ip_3, 11000);
                //socket.send(out_3);


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
    }
}




