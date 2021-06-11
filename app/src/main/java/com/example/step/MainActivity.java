package com.example.step;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.step.directionhelpers.FetchURL;
import com.example.step.directionhelpers.TaskLoadedCallback;
import com.example.step.ui.dashboard.DirectionsAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, TaskLoadedCallback {

    FirebaseDatabaseHelper fdb;


    public static final String SHARED_PREFS = "sharedPrefs";
    public String userUID;
    public float todaysKilometers;
    public float weeksKilometers;
    public float monthsKilometers;
    public String strToday;
    public String month;
    public String day;

    public Date currentDate;

    public Boolean record;

    public LocationManager manager;
    public float kilometers;
    public Location currLocation;
    public double currSpeed;

    //maps thinks
    public SupportMapFragment fragment;
    public GoogleMap map;

    //direction thinks
    public ArrayList<Location> recordedPoints;
    private Polyline currentPolyline;

    //maps view thinks
    public TextView kilos;
    public TextView velocity;

    public ArrayList<Day> dailyStats;
    public ArrayList<Week> weeklyStats;
    public ArrayList<Month> monthlyStats;
    public ArrayList<String> directories;


    List<String> listGroup;
    HashMap<String, List<String>> listItem;
    DirectionsAdapter adapter;
    ArrayList<MapPoint> mapPoints;


    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat daysFormat = new SimpleDateFormat("EEEE");
    public String monday = daysFormat.format(new Date(2020, 3, 19));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //database
        setContentView(R.layout.activity_main);

        loadUserUID();
        fdb = new FirebaseDatabaseHelper(userUID);
        dailyStats = new ArrayList<>();
        weeklyStats = new ArrayList<>();
        monthlyStats = new ArrayList<>();
        directories = new ArrayList<>();
        mapPoints = new ArrayList<>();
        setDailyStats(fdb.getReferenceDays());
        setWeeklyStats(fdb.getReferenceWeeks());
        setMonthlyStats(fdb.getReferenceMonths());
        setDirectories(fdb.getReferenceDirectories());

        recordedPoints = new ArrayList<>();

        //Toast.makeText(MainActivity.this,"Main activity "+userUID,Toast.LENGTH_LONG).show();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        checkChangeDay(0);
        currSpeed = 0;

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "you need enable the gps", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            finish();
            return;
        }
        BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Toast.makeText(MainActivity.this, "Go to welcome", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(locationSwitchStateReceiver, filter);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        currLocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        kilometers = 0;
        //maps view thinks
    }

    public void ClearMap(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want clear the map?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        map.clear();
                        kilos.setAlpha(0.0f);
                        if(record!=null && record){
                            rebuildMap();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public FirebaseDatabaseHelper getFdb() {
        return fdb;
    }
    public ArrayList<Day> getDailyStats() {
        return dailyStats;
    }
    public void setDailyStats(DatabaseReference ref){
        Query reference = ref.limitToLast(7);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dailyStats.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Day day = dataSnapshot1.getValue(Day.class);
                    dailyStats.add(day);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void setWeeklyStats(DatabaseReference ref){
        Query reference = ref.limitToLast(4);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weeklyStats.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Week week = dataSnapshot1.getValue(Week.class);
                    weeklyStats.add(week);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public ArrayList<Week> getWeeklyStats() {
        return weeklyStats;
    }
    public ArrayList<Month> getMonthlyStats() {
        return monthlyStats;
    }
    public void setMonthlyStats(DatabaseReference ref){
        Query reference = ref.limitToLast(12);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                monthlyStats.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Month month = dataSnapshot1.getValue(Month.class);
                    monthlyStats.add(month);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setDirectories(DatabaseReference ref){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                directories.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    directories.add(dataSnapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setMapPoints(String name){
        DatabaseReference ref=fdb.getReferenceDirectories();
        mapPoints.clear();
        ref.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    MapPoint point = dataSnapshot1.getValue(MapPoint.class);
                    mapPoints.add(point);
                }
                buildOldMapFromDirection(mapPoints);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void initContentMapView(TextView kilos,TextView velocity,SupportMapFragment fragment) {
        this.kilos=kilos;
        this.velocity=velocity;
        this.fragment=fragment;
        setContentMapView();

    }
    public Boolean getRecord() {
        return record;
    }
    public void setContentMapView(){
        String text=String.format("%.2f",kilometers)+" m\n";
        kilos.setText(text);
        velocity.setText(String.format("%.2f",currSpeed)+" km/s");
        fragment.getMapAsync(this);
    }
    public void setRecord(@NotNull Boolean record){
        this.record=record;

    }

    public void saveDirection(){
        final ArrayList<Location> points =new ArrayList<>(recordedPoints);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("do you want save this direction?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this,"yes save",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Set directions title");
                        builder.setCancelable(false);
                        final EditText input = new EditText(MainActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String directionsTitle;
                                directionsTitle=input.getText().toString();
                                Toast.makeText(MainActivity.this,"Save ok",Toast.LENGTH_LONG).show();

                                fdb.addDirectory(directionsTitle,points);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this,"Save canceled ",Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        kilometers=0;
        map.clear();
        recordedPoints.clear();
    }
    public void directoriesList(final ExpandableListView directionsListView){
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new DirectionsAdapter(this,listGroup,listItem);
        directionsListView.setAdapter(adapter);
        directionsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                final String name=(String)adapter.getChild(groupPosition,childPosition);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Choose Activity")
                        .setCancelable(false)
                        .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        setMapPoints(name);
                                        directionsListView.collapseGroup(0);
                                    }
                                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fdb.deleteDirectory(name);
                        directionsListView.collapseGroup(0);

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }

        });

        listGroup.add("directories");
        listItem.put(listGroup.get(0),directories);
        adapter.notifyDataSetChanged();

    }


    public void setFirstPoint(){
        if(currLocation!=null) {
            recordedPoints.add(currLocation);
            if (map != null) {
                map.addMarker(new MarkerOptions().position(new LatLng(currLocation.getLatitude(), currLocation.getLongitude())).title("start"));
                LatLng latLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }
    public void setNewPoint(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        if(recordedPoints.isEmpty()) {
            recordedPoints.add(location);
            map.addMarker(new MarkerOptions().title("first point").position(latLng));
        }
        else {
            recordedPoints.add(location);
            if (map != null) {
                Log.d("mylog", "Added Markers");
                String url = getUrl(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()), "walking");
                new FetchURL(MainActivity.this).execute(url, "walking");
            }
        }
    }


    //location manager functions
    @Override
    public void onLocationChanged(Location location) {

        //float []results = new float[1];
        //Toast.makeText(this,"location changed",Toast.LENGTH_LONG).show();
        if(currLocation!=null){
            float result=location.distanceTo(currLocation);
            float accuracy = location.getAccuracy()+currLocation.getAccuracy();
            //Location.distanceBetween(location.getLatitude(),location.getLongitude(),getCurrLocation().getLatitude(),getCurrLocation().getLongitude(),results);
            //Toast.makeText(this,"accuracy = "+accuracy+" distance ="+result,Toast.LENGTH_LONG).show();
            if (accuracy<result){
                if(record!=null && record){
                    kilometers=kilometers+result;
                    setNewPoint(location);
                }
                currLocation=location;
                currSpeed=location.getSpeed();
                if(map!=null) {
                    LatLng latLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
                if (kilos != null) {
                    kilos.setText(String.format("%.2f",kilometers) + " m");
                }
                if (velocity != null) {
                    velocity.setText(String.format("%.2f",currSpeed) + " km/s");
                }
                //add results to daily,weekly and monthly kiloeters
                checkChangeDay(result);
            }
            else{
                //Toast.makeText(MainActivity.this,"maybe you dont change location",Toast.LENGTH_LONG).show();
            }

        }
        else{
            currLocation=location;
            currSpeed=location.getSpeed();

            if (map != null) {
                LatLng latLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
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
    @Override

    //open map
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        if(currLocation==null){
            //Toast.makeText(this, "null location", Toast.LENGTH_SHORT).show();
        }
        else {
            LatLng latLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
        map.setMyLocationEnabled(true);

        if(record!=null && record){
            rebuildMap();
        }
        //Toast.makeText(this, "Called Init Map Ready", Toast.LENGTH_SHORT).show();

    }
    //open map with current direction
    public void rebuildMap(){
        //Toast.makeText(this,"rebuild",Toast.LENGTH_LONG).show();
        Location from=null;
        if(!recordedPoints.isEmpty()) {
            for(Location to : recordedPoints){
                if(from==null){
                    map.addMarker(new MarkerOptions().position(new LatLng(to.getLatitude(), to.getLongitude())).title("start"));
                }
                else{
                    Log.d("mylog", "Added Markers");
                    String url = getUrl(new LatLng(from.getLatitude(), from.getLongitude()), new LatLng(to.getLatitude(), to.getLongitude()), "walking");
                    new FetchURL(MainActivity.this).execute(url, "walking");
                }
                from = to;
            }
        }
        if(from!=null){
            LatLng latLng = new LatLng(from.getLatitude(), from.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
    }
    //add on map a old direction
    public void buildOldMapFromDirection(ArrayList<MapPoint> points){
        //Toast.makeText(this,"build map",Toast.LENGTH_LONG).show();
        float directoriesKilometers=0;
        float []results = new float[1];
        MapPoint from=null;
        for (MapPoint to : points) {
            if (from == null) {
                map.addMarker(new MarkerOptions().position(new LatLng(to.getLat(), to.getLng())).title("start"));
                LatLng latLng = new LatLng(to.getLat(), to.getLng());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            }
            else{
                Location.distanceBetween(from.getLat(),from.getLng(),to.getLat(),to.getLng(),results);
                directoriesKilometers+=results[0];
                Log.d("mylog", "Added Markers");
                String url = getUrl(new LatLng(from.getLat(), from.getLng()), new LatLng(to.getLat(), to.getLng()), "walking");
                new FetchURL(MainActivity.this).execute(url, "walking");
            }
            from = to;
        }
        Toast.makeText(MainActivity.this,"directories distance is "+String.format("%.2f",directoriesKilometers)+" m",Toast.LENGTH_LONG).show();
    }




    //write directory in map
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
    @Override
    public void onTaskDone(Object... values) {
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    //Share Preferences : 1.userid in firebase auth and database 2.kilometers today,week,month and 3.goal daily,weekly,monthly
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("todaysKilometers",todaysKilometers);
        editor.putString("today",dateFormat.format(currentDate));
        editor.putString("day",daysFormat.format(currentDate));
        editor.putFloat("weeksKilometers",weeksKilometers);
        editor.putFloat("monthsKilometers",monthsKilometers);
        editor.putString("month",monthFormat.format(currentDate));
        editor.apply();
        //Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        todaysKilometers = sharedPreferences.getFloat("todaysKilometers",0);
        strToday = sharedPreferences.getString("today","");
        day = sharedPreferences.getString("day","");
        weeksKilometers = sharedPreferences.getFloat("weeksKilometers",0);
        monthsKilometers = sharedPreferences.getFloat("monthsKilometers",0);
        month = sharedPreferences.getString("month","");
    }
    public void loadUserUID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userUID = sharedPreferences.getString("userUID","");
    }
    public void checkChangeDay(float extraKilos){
        loadData();
        Calendar c = Calendar.getInstance();
        currentDate=c.getTime();
        if(!day.equals("")) {
            if (!strToday.equals(dateFormat.format(c.getTime()))) {
                //Toast.makeText(this, "changed day", Toast.LENGTH_LONG).show();
                todaysKilometers = todaysKilometers / 1000;
                fdb.addDay(strToday, day, (float) todaysKilometers);
                if (monday.equals(daysFormat.format(currentDate))) {
                    //Toast.makeText(this, "changed week", Toast.LENGTH_LONG).show();
                    fdb.addWeek(weeksKilometers/1000);

                    weeksKilometers = extraKilos;
                }
                if (!month.equals(monthFormat.format(currentDate))) {
                    //Toast.makeText(this, "changed month", Toast.LENGTH_LONG).show();
                    fdb.addMonth(month,monthsKilometers/1000);
                    monthsKilometers = extraKilos;
                }
                todaysKilometers = extraKilos;
            } else {
                weeksKilometers = weeksKilometers + extraKilos;
                monthsKilometers = monthsKilometers + extraKilos;
                todaysKilometers = todaysKilometers + extraKilos;
                //Toast.makeText(this, "today's kilometers = " + todaysKilometers, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "week's kilometers = " + weeksKilometers, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "month's kilometers = " + monthsKilometers, Toast.LENGTH_LONG).show();
            }
        }
        saveData();
    }
    public void setDailyGoal(float dailyGoal){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("dailyGoal",dailyGoal);
        editor.apply();
        Toast.makeText(this,"Daily goal set ok",Toast.LENGTH_LONG).show();
    }
    public float getDailyGoal(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getFloat("dailyGoal",0);
    }
    public float getTodaysKilometers() {
        return todaysKilometers/1000;
    }
    public void setWeeklyGoal(float weeklyGoal){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("weeklyGoal",weeklyGoal);
        editor.apply();
        Toast.makeText(this,"Weekly goal set ok",Toast.LENGTH_LONG).show();
    }
    public float getWeeklyGoal(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getFloat("weeklyGoal",0);
    }
    public float getWeeksKilometers() {
        return weeksKilometers/1000;
    }
    public void setMonthlyGoal(float weeklyGoal){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("monthlyGoal",weeklyGoal);
        editor.apply();
        Toast.makeText(this,"Monthly goal set ok",Toast.LENGTH_LONG).show();
    }
    public float getMonthlyGoal(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getFloat("monthlyGoal",0);
    }
    public float getMonthsKilometers() {
        return monthsKilometers/1000;
    }

}