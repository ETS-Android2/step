package com.example.step.ui.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.step.MainActivity;
import com.example.step.R;
import com.example.step.ui.notifications.CLocation;

public class LocationActivity extends AppCompatActivity implements LocationListener {
    int kilometers;
    String buff;

    public LocationActivity(){
        kilometers=101;
        buff="gamw thn psyxh mou";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        kilometers=100;
        buff="oook man mou";
    }

    @Override
    public void onLocationChanged(Location location) {
        buff=buff+"u";
        kilometers++;
        Toast.makeText(getApplicationContext(),"location change",Toast.LENGTH_LONG).show();
    }

    public int getKilometers() {
        return kilometers;

    }

    public String getBuff() {
        return buff;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
