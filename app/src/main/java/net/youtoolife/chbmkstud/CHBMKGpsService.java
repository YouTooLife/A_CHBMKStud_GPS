package net.youtoolife.chbmkstud;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CHBMKGpsService extends Service {


    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    public static final String BROADCAST_ACTION = "CHBMK_GPS_SERVIVES";
    public LocationManager locationManager;
    public LocationListener mLocationListener;


    private BroadcastReceiver locationUpdateRec = null;

    public static Boolean isRunning = false;

    Context context;

    Intent intent;

    public CHBMKGpsService() {

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        mHandlerTask.run();
        return START_STICKY;
    }



    @Override
    public void onCreate() {

        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);
        context=this;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ResumePermission","No permission");
            return;
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener();

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5, mLocationListener);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, mLocationListener);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            locationUpdateRec = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {

                        Log.d("LocationUR", "update location");
                        String key = LocationManager.KEY_LOCATION_CHANGED;
                        Location location = (Location) intent.getExtras().get(key);

                        if (location != null) {
                            Log.d("LocationUR", "sent");
                            sendLocation(location);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            registerReceiver(locationUpdateRec, new IntentFilter());
        }


    }

    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            if (!isRunning) {
                startListening();
            }
            mHandler.postDelayed(mHandlerTask, 5000);
        }
    };



    @Override
    public IBinder onBind(Intent intent){
        return null;
    }



    @Override
    public void onDestroy() {
        stopListening();
        if (locationUpdateRec != null)
            unregisterReceiver(locationUpdateRec);
        mHandler.removeCallbacks(mHandlerTask);
        super.onDestroy();
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5, mLocationListener);

            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, mLocationListener);
        }
        isRunning = true;
    }

    private void stopListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null && mLocationListener != null)
                locationManager.removeUpdates(mLocationListener);
        }
        isRunning = false;
    }



    public class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            sendLocation(location);

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                intent.putExtra("PROVIDER", 0);
                intent.putExtra("GPS_LAT", location.getLatitude());
                intent.putExtra("GPS_LONG", location.getLongitude());

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", 1);

            } else if (location.getProvider().equals(
                    LocationManager.NETWORK_PROVIDER)) {
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_LAT", location.getLatitude());
                intent.putExtra("NETWORK_LONG", location.getLongitude());
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", 1);
            }
            sendBroadcast(intent);
            Log.d("CHBMK::GPS","sendBroad");
        }



        @Override
        public void onProviderDisabled(String provider) {
            stopListening();

            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                intent.putExtra("PROVIDER", 0);

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", 1);

            } else if (provider.equals(
                    LocationManager.NETWORK_PROVIDER)) {
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", 1);
            }
            sendBroadcast(intent);
        }


        @Override
        public void onProviderEnabled(String provider) {

            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                intent.putExtra("PROVIDER", 0);

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", 1);

            } else if (provider.equals(
                    LocationManager.NETWORK_PROVIDER)) {
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", 1);
            }
            sendBroadcast(intent);
            /*
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("ProvEnabledPermission","No permission");
                return;
            }
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(provider);
                intent.putExtra("PROVIDER", 0);
                intent.putExtra("GPS_LAT", location.getLatitude());
                intent.putExtra("GPS_LONG", location.getLongitude());

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", 1);

            } else if (provider.equals(
                    LocationManager.NETWORK_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(provider);
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_LAT", location.getLatitude());
                intent.putExtra("NETWORK_LONG", location.getLongitude());
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", 1);
            }*/

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                intent.putExtra("PROVIDER", 0);

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", status);

            } else if (provider.equals(
                    LocationManager.NETWORK_PROVIDER)) {
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", status);
            }
            sendBroadcast(intent);
            /*
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("ProvEnabledPermission","No permission");
                return;
            }
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(provider);
                intent.putExtra("PROVIDER", 0);
                intent.putExtra("GPS_LAT", location.getLatitude());
                intent.putExtra("GPS_LONG", location.getLongitude());

                intent.putExtra("GPS_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                intent.putExtra("GPS_PROV_STAT", status);

            } else if (provider.equals(
                    LocationManager.NETWORK_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(provider);
                intent.putExtra("PROVIDER", 1);
                intent.putExtra("NETWORK_LAT", location.getLatitude());
                intent.putExtra("NETWORK_LONG", location.getLongitude());
                intent.putExtra("NETWORK_PROV_ENABLED",
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                intent.putExtra("NETWORK_PROV_STAT", status);
            }
            */
        }
    }
    //------End listener-----//


    private void sendLocation(Location locat) {

        if (locat == null)
            return;

        String location = String.valueOf(locat.getLatitude())+";"+String.valueOf(locat.getLongitude());

        if (location == null || location.isEmpty())
            return;

        String invite = null, login = null, parent = null;

        if (SharedPrefManager.getInstance(getApplicationContext()).getInvite() != null
                && SharedPrefManager.getInstance(getApplicationContext()).getLogin()  != null
                && SharedPrefManager.getInstance(getApplicationContext()).getParent()  != null) {
            invite = SharedPrefManager.getInstance(getApplicationContext()).getInvite();
            login = SharedPrefManager.getInstance(getApplicationContext()).getLogin();
            parent = SharedPrefManager.getInstance(getApplicationContext()).getParent();
        }
        else
            return;

        Map<String, String> params0 = new HashMap<>();
        //params0.put("dev", RSAIsa.rsaEncrypt(SharedPrefManager.getInstance(getApplicationContext()).getToken()));
        params0.put("login", login);
        params0.put("invite", RSAIsa.rsaEncrypt(invite));
        params0.put("location", RSAIsa.rsaEncrypt(location));
        params0.put("parent", parent);
        params0.put("pwd", RSAIsa.rsaEncrypt(XA.b(XA.B)));

        JSONObject jsonObject = new JSONObject(params0);
        Map<String, String> params = new HashMap<>();
        String json = jsonObject.toString();
        System.out.print("json " + json);
        try {
            params.put("d", Base64.encodeToString(jsonObject.toString().getBytes("UTF-8"), Base64.DEFAULT));
            //params.put("d", RSAIsa.rsaEncrypt(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("i", "i");

        //RequestHandler requestHandler = new RequestHandler(XA.b(XA.A), params, getApplicationContext());
        RequestHandler requestHandler = new RequestHandler("http://chbmk.000webhostapp.com/chbmk/stud_login.php", params, getApplicationContext());
        requestHandler.request(new CallBack() {
            @Override
            public void callBackFunc(String response) {
                try {
                    if (response == null)
                        return;
                    Log.d("sender: answer", response);
                    JSONObject obj = new JSONObject(response);
                    int id = obj.getInt("id");
                    if (id > -1) {
                        Log.d("Auth", "Its ok!");
                    } else {
                        Log.d("Auth", "Auth Error!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
