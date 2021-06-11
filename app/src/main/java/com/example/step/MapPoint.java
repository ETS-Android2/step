package com.example.step;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public class MapPoint {

    double lat;
    double lng;

    public MapPoint() {
    }

    public MapPoint(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;
    }

    public MapPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;

    }

    public MapPoint(Location loc){
        lat=loc.getLatitude();
        lng = loc.getLongitude();

    }


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
