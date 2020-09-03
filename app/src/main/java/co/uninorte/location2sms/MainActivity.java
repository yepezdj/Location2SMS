package co.uninorte.location2sms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Initialize variable
    Button btLocation;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    String txtMessage;
    Button btnsnd2;
    EditText editip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assing variable
        btLocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);
        btnsnd2 = (Button) findViewById(R.id.btnSend2);
        editip = findViewById(R.id.Edit_ip);




        //Initialize focusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    getLocation();
                } else {
                    //When permission denied
                    ActivityCompat.requestPermissions(MainActivity.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void enviarr(View v)
    {
        EnviarTCP enviar = new EnviarTCP();
        enviar.execute(txtMessage);

        UDPClient enviaar = new UDPClient();
        enviaar.execute(txtMessage);

        Toast.makeText(getApplicationContext(), "Coordenadas enviadas", Toast.LENGTH_SHORT).show();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocation();
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
                                location.getLatitude(), location.getLongitude(), 1
                        );
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
                        //Set sms
                        txtMessage  = ("Latitud: " + addresses.get(0).getLatitude()+ "\n" +
                                "Longitud: "+ addresses.get(0).getLongitude() +"\n" +
                                "TimeStamp: "+ new Date().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @SuppressWarnings("deprecation")
    public class EnviarTCP extends AsyncTask<String,Void,Void>
    {
        Socket s;
        PrintWriter pw;

        @Override
        protected Void doInBackground(String... voids) {

            String message = voids[0];

            try
            {
                s = new Socket(editip.getText().toString(), 10000);
                pw= new PrintWriter(s.getOutputStream());
                pw.write(message);
                pw.flush();
                pw.close();
                s.close();

            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


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

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }


    }
}


