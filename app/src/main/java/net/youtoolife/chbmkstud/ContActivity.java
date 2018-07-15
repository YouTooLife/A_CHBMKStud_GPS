package net.youtoolife.chbmkstud;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class ContActivity extends AppCompatActivity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;

    //private LocationManager locationManager;

    private BroadcastReceiver broadcastReceiver;

    private Intent mainIntent;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont);


        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BROADCAST", "recei");

                if (intent.getIntExtra("PROVIDER", -1) == 0) {

                    boolean enabled = intent.getBooleanExtra("GPS_PROV_ENABLED", false);
                    int status = intent.getIntExtra("GPS_PROV_STAT", -1);
                    double longi = intent.getDoubleExtra("GPS_LONG", -1);
                    double lat = intent.getDoubleExtra("GPS_LAT", -1);

                    tvStatusGPS.setText("Status: " + String.valueOf(status));
                    tvEnabledGPS.setText("Enabled: "+String.valueOf(enabled));
                    tvLocationGPS.setText("longitude:\n"+String.valueOf(longi)+"\nlatitude:\n"+String.valueOf(lat));
                }
                else {
                    boolean enabled = intent.getBooleanExtra("NETWORK_PROV_ENABLED", false);
                    int status = intent.getIntExtra("NETWORK_PROV_STAT", -1);
                    double longi = intent.getDoubleExtra("NETWORK_LONG", -1);
                    double lat = intent.getDoubleExtra("NETWORK_LAT", -1);

                    tvStatusNet.setText("Status: " + String.valueOf(status));
                    tvEnabledNet.setText("Enabled: "+String.valueOf(enabled));
                    tvLocationNet.setText("longitude:\n"+String.valueOf(longi)+"\nlatitude:\n"+String.valueOf(lat));
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(CHBMKGpsService.BROADCAST_ACTION));

        mainIntent = new Intent(this, CHBMKGpsService.class);
        startService(mainIntent);
    }

    public void onClickUpdate(View view) {
        if (mainIntent != null)
        {
            stopService(mainIntent);
            mainIntent = new Intent(this, CHBMKGpsService.class);
            startService(mainIntent);
        }
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        //startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
        //startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
        //turnGPSOnOff();
    }

    public  void  onClickProviderSettings(View view) {
        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        startActivity(i);
        finishActivity(0);
    }

    private void turnGPSOnOff(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
            //Toast.makeText(this, "Your GPS is Enabled",Toast.LENGTH_SHORT).show();
            } }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        Log.d("onStop", "stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy", "destroy");
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
