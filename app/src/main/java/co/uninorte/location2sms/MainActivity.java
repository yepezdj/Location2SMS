package co.uninorte.location2sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Initialize variable
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    String txtMessage;
    EditText editip;
    @SuppressWarnings("deprecation")
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assing variable
        //btLocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);
        //cancel = (Button) findViewById(R.id.cancel);
        editip = findViewById(R.id.Edit_ip);

        //Initialize focusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    //Envio periodico
    public void startRepeating(View v) {
        //mHandler.postDelayed(mToastRunnable, 5000);
        EnvioRunnable.run();
    }

    public void stopRepeating(View v) {
        mHandler.removeCallbacks(EnvioRunnable);
        enviarr();
    }

    private final Runnable EnvioRunnable = new Runnable() {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(MainActivity.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                getLocation();
            } else {
                //When permission denied
                ActivityCompat.requestPermissions(MainActivity.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }

            mHandler.postDelayed(this, 5000);

        }
    };


    @SuppressWarnings("deprecation")
    public void enviarr()
    {
        new UDPClient().execute(txtMessage);
    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //getLocation();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        //Set latitude on TextView
                        textView1.setText(Html.fromHtml(
                                "<font color= '#6200EE'><b>Latitud :</b><br></font>"
                                        + addresses.get(0).getLatitude()
                        ));
                        //Set longitude on TextView
                        textView2.setText(Html.fromHtml(
                                "<font color= '#6200EE'><b>Longitud :</b><br></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        //Set country name
                        textView3.setText(Html.fromHtml(
                                "<font color= '#6200EE'><b>País :</b><br></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        //Set locality
                        textView4.setText(Html.fromHtml(
                                "<font color= '#6200EE'><b>Ciudad :</b><br></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        //Set address
                        textView5.setText(Html.fromHtml(
                                "<font color= '#6200EE'><b>Dirección :</b><br></font>"
                                        + addresses.get(0).getAddressLine(0)
                        ));
                        //Set message
                        txtMessage  = ("\n" +
                                "Latitud: " + addresses.get(0).getLatitude()+ ",\n" +
                                "Longitud: "+ addresses.get(0).getLongitude() +",\n" +
                                "TimeStamp: "+ new Date().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class UDPClient  extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try{
                String message = voids[0];

                InetAddress ip=InetAddress.getByName(editip.getText().toString());
                DatagramSocket socket=new DatagramSocket();
                byte[] outData = (message).getBytes();

                DatagramPacket out = new DatagramPacket(outData,outData.length,ip ,11000);
                socket.send(out);

                //Toast.makeText(getApplicationContext(), "Send UDP", Toast.LENGTH_SHORT).show();

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }


}


