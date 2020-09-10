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
        //textView4 = findViewById(R.id.text_view4);
        //textView5 = findViewById(R.id.text_view5);
        //cancel = (Button) findViewById(R.id.cancel);
        editip = findViewById(R.id.Edit_ip);

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
                            + location.getLatitude()
            ));
            //Set time stamp
            textView3.setText(Html.fromHtml(
                    "<font color= '#6200EE'><b>TimeStamp :</b><br></font>"
                            + new Date().toString()));
            //Set message
            txtMessage = ("\n" +
                    "Latitud:_"+ location.getLatitude()+",\n"+
                    "Longitud:_"+location.getLatitude()+",\n"+
                    "TimeStamp:_"+new Date().toString());
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
            mHandler.postDelayed(this, 5000);
        }
    };

    
    public void enviarr() {
        //noinspection deprecation
        new UDPClient().execute(txtMessage);
    }




    /*public void getLocation() {
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
                                "<font color= '#6200EE'><b>TimeStamp :</b><br></font>"
                                        +new Date().toString()
                        ));
                        //Set message
                        txtMessage = ("\n" +
                                "Latitud: "+addresses.get(0).getLatitude()+",\n"+
                                "Longitud: "+addresses.get(0).getLongitude()+",\n"+
                                "TimeStamp: "+new Date().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }*/


    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class UDPClient extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {
                String message = voids[0];

                InetAddress ip = InetAddress.getByName(editip.getText().toString());
                DatagramSocket socket = new DatagramSocket();
                byte[] outData = (message).getBytes();

                DatagramPacket out = new DatagramPacket(outData, outData.length, ip, 11000);
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




