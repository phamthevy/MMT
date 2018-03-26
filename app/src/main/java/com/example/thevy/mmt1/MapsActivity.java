package com.example.thevy.mmt1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.os.Build.VERSION_CODES.M;
import static com.example.thevy.mmt1.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button button_connect;
    private Button button_findMatch;
    private Button button_quit;
    static private EditText textView_name;
    private EditText textView_time;

    static TextView textView_battleStatus;
    static TextView textView_result;

    //private String IPAddress = "192.168.100.4";

    private int port = 2313;

    private TextView textView_IPApp;
    private TextView textView_IPGateway;
    private EditText textView_IPAppc;
    private EditText textView_IPGatewayc;


    private LocationManager locationManager;
    private LocationListener listener;
    int time;
    static Marker marker1=null,marker2=null,marker3=null;
    static LatLng YOU=null, OPPONENT=null, FINISH=null;

    static double longitude, latitude;

    //UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;
    TcpClientThread tcpClientThread;


    static GoogleMap mMap;
    static String dataSendO="10.880602_106.807775";
    static String dataSendT="10.880602_110.807675";
    static String dataStatus="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used


        button_connect = (Button)findViewById(R.id.button_connect);
        button_findMatch = (Button)findViewById(R.id.button_findmatch);
        button_quit = (Button)findViewById(R.id.button_quit);
        textView_name = (EditText) findViewById(R.id.textView_name);
        textView_time = (EditText) findViewById(R.id.textView_time);
        textView_battleStatus = (TextView) findViewById(R.id.textView_battleStatus);
        textView_result = (TextView) findViewById(R.id.textView_result);
        textView_IPApp = (TextView) findViewById(R.id.textView_IPApp);
        textView_IPAppc = (EditText) findViewById(R.id.textView_IPAppc);
        textView_IPGateway = (TextView) findViewById(R.id.textView_IPGateway);
        textView_IPGatewayc = (EditText) findViewById(R.id.textView_IPGatewayc);

        button_findMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_findMatch.setEnabled(false);
                textView_battleStatus.setText("waiting...");
                textView_name.setEnabled(false);
                //tcpClientThread = new TcpClientThread(longitude+"_"+latitude);
                tcpClientThread = new TcpClientThread(textView_IPAppc.getText().toString(),longitude+"_"+latitude );
                tcpClientThread.start();

            }
        });

        button_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                you(longitude+"_"+latitude);
                opponent(dataSendO);
                target(dataSendT);
                udpClientThread = new UdpClientThread( textView_IPGatewayc.getText().toString(),port,longitude+"_"+latitude );
                udpClientThread.start();
                if (dataStatus.equals("1")){ textView_battleStatus.setText("Connected"); }
                if (dataStatus.equals("2")){ textView_result.setText("YOU WIN"); }
                if (dataStatus.equals("3")){ textView_result.setText("YOU LOSE"); }

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
                //noinspection MissingPermission
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_time.setVisibility(View.GONE);
                button_connect.setVisibility(View.GONE);
                textView_name.setVisibility(View.VISIBLE);
                button_findMatch.setVisibility(View.VISIBLE);
                button_quit.setVisibility(View.VISIBLE);
                textView_IPApp.setVisibility(View.INVISIBLE);
                textView_IPAppc.setVisibility(View.INVISIBLE);
                textView_IPGateway.setVisibility(View.INVISIBLE);
                textView_IPGatewayc.setVisibility(View.INVISIBLE);

                time = Integer.parseInt(textView_time.getText().toString());
                textView_time.setEnabled(false);

                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", time, 0, listener);

                
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    public static void you(String loc){
        if(marker1==null){
        YOU = new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
         MarkerOptions you = new MarkerOptions().position(YOU).title(textView_name.getText().toString());
        marker1=mMap.addMarker(you);
            marker1.setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YOU,5));
        }
        else {
            YOU= new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
            MarkerOptions you = new MarkerOptions().position(YOU).title(textView_name.getText().toString());
            marker1.remove();
            marker1=mMap.addMarker(you);
            marker1.setVisible(true);
        }
    }
    public static void target(String loc){
        if(marker2==null){
            FINISH = new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
            MarkerOptions finish = new MarkerOptions().position(FINISH).title("FINISH");
            marker2=mMap.addMarker(finish);
            marker2.setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FINISH,1));
        }
        else {
            FINISH= new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
            MarkerOptions finish = new MarkerOptions().position(FINISH).title("FINISH");
            marker2.remove();
            marker2=mMap.addMarker(finish);
            marker2.setVisible(true);
        }
    }
    public static void opponent(String loc){
        if(marker3==null){
            OPPONENT = new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
            MarkerOptions opp = new MarkerOptions().position(OPPONENT).title("OPPONENT");
            marker3=mMap.addMarker(opp);
            marker3.setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(OPPONENT,1));
        }
        else {
            OPPONENT= new LatLng(Double.parseDouble(loc.split("_")[1]), Double.parseDouble(loc.split("_")[0]));
            MarkerOptions opp = new MarkerOptions().position(OPPONENT).title("OPPONENT");
            marker3.remove();
            marker3=mMap.addMarker(opp);
            marker3.setVisible(true);
        }
    }
    public static void getDataO(String data){
        dataSendO = data;
    }
    public static void getDataT(String data){
        dataSendT = data;
    }
    public static void getStatus(String data) { dataStatus = data; }

}
