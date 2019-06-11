package com.posseggs.malapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationReader implements LocationListener {

    private static String TAG = "LocationReader";
    private String filename = "Locations.txt"; //Location where to store locations
    private boolean append = true; //Default true

    private Context context;

    public LocationReader(Context context)
    {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location loc) {


        String newLocation = "Location changed:\n Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude();

        String cityName = "unknown";
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try //Try to get city from latitude and longitude
        {
            addresses = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
            if (addresses.size() > 0)
            {
                cityName = addresses.get(0).getLocality(); //Get first address
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, e.getMessage());
        }
        if (cityName == null) //If cityName was not found, set city to unknown
            cityName = "unknown";

        String cityLocation = "\nCurrent City is: " + cityName;

        String output = newLocation + cityLocation;

        //Save location and possible city to file
        FileIOHelper.saveToFile(filename, output, append);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}
