package co.uninorte.location2sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, SensorEventListener {
    //Initialize variable
    TextView textView1, textView2, textView3;
    FusedLocationProviderClient fusedLocationProviderClient;
    private SensorManager sensorManager;

    String txtMessage;
    Integer truck = 1;
    //Direcciones Ip's de las instancias de AWS


    String ip1 = "3.215.220.179";
    String ip2 = "54.227.210.76";
    String ip3 = "18.204.193.250";
    String ip4 = "167.0.214.64";
    String xtext, ytext, ztext;

    @SuppressWarnings("deprecation")
    private Handler mHandler = new Handler();

    public MainActivity() {
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assing variable
        SensorManager smm;
        List<Sensor> sensor;
        ListView lv;

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

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event){
        // check sensor type
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            xtext = String.valueOf(x);
            ytext = String.valueOf(y);
            ztext = String.valueOf(z);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                truck = 1;
                return true;
            case R.id.item2:
                truck = 2;
                return true;
            default:
                return false;
        }
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
            String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
            txtMessage = (location.getLatitude()+","+location.getLongitude()+","+(DateFormat.format("yyyy-MM-ddTHH:mm:ss", new java.util.Date()).toString())+","+ truck+","+xtext+"/"+ytext+"/"+ztext);
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
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, 4000);
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

                InetAddress ip_1 = InetAddress.getByName(ip1);
                InetAddress ip_2= InetAddress.getByName(ip2);
                InetAddress ip_3 = InetAddress.getByName(ip3);
                InetAddress ip_4 = InetAddress.getByName(ip4);
                DatagramSocket socket = new DatagramSocket();
                byte[] outData = (message).getBytes();

                DatagramPacket out_1 = new DatagramPacket(outData, outData.length, ip_1, 11000);
                socket.send(out_1);
                DatagramPacket out_2 = new DatagramPacket(outData, outData.length, ip_2, 11000);
                socket.send(out_2);
                DatagramPacket out_3 = new DatagramPacket(outData, outData.length, ip_3, 11000);
                socket.send(out_3);
                DatagramPacket out_4 = new DatagramPacket(outData, outData.length, ip_4, 11000);
                socket.send(out_4);


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
    }
}




