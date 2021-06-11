package com.example.step;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WelcomeActivity extends AppCompatActivity {
    TextView welcome_message;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcome_message=(TextView)findViewById(R.id.welcome_message);
        welcome_message.setText("Please get permission and enable gps");

        if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WelcomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else
            {
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    return;
                }
                welcome_message.setText("please enable gps");
                //Toast.makeText(WelcomeActivity.this,"Welcome ",Toast.LENGTH_LONG).show();
                BroadcastReceiver  locationSwitchStateReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            //Toast.makeText(WelcomeActivity.this,"go to main activity",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                            return;
                        }
                    }
                };
                IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
                filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
                registerReceiver(locationSwitchStateReceiver,filter);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
                        WelcomeActivity.this.finish();
                        return;
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    WelcomeActivity.this.finish();
                    return;
                }
                return;
            }
        }
    }


}
