package com.sergioapps.infomagnet;


import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;



public class GPSTracker extends Service implements LocationListener {

    private volatile static String address = "";
	private volatile static String city = "";
	private volatile static String state = "";
	private volatile static String ctry = "";
    private volatile static String pc = "";   
    
    private final Context mContext;
 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
 
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                isNetworkEnabled = true;
            else isNetworkEnabled = false;
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
			//////////////////////////////////////////////////////////////////////////////////////////////////
			Geocoder geoCoder = new Geocoder(mContext, Locale.getDefault());
			List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++) {
                    address += addresses.get(0).getAddressLine(i) + "\n";
                }
				city = addresses.get(0).getLocality();  	// Locality of address, i.e "Mountain View", or null
				state = addresses.get(0).getAdminArea();  	// Administrative area, i.e. "CA", or null
				ctry = addresses.get(0).getCountryName();	// Country
				pc = addresses.get(0).getPostalCode();  	// Postal Code	
			}				
			//////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    @SuppressLint("MissingPermission")
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }       
    }
     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }

    public String getAddress(){
        if(address != null){
            return address;
        } else {
            return "";
        }
    }

    public String getCityStr(){
        if(city != null){
        	return city;
        } else {
        	return "";
        }
    }    
 
    public String getStateStr(){
        if(state != null){
        	return state;
        } else {
        	return "";
        }
    }
    
    public String getCtryStr(){
        if(ctry != null){
        	return ctry;
        } else {
        	return "";
        }
    } 
    
    public String getPCStr(){
        if(pc != null){
        	return pc;
        } else {
        	return "";
        }
    }    
    
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
 
    @Override
    public void onLocationChanged(Location location) {
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
}



//	String city = "";
//	String state = "";
//	String ctry = "";
//	String pc = "";
//	String lat = "0.0";
//	String lng = "0.0";
//	
//
//				//////////////////////////////////////////////////////////////////////////////////////////////////
//				Geocoder geoCoder = new Geocoder(mContext, Locale.getDefault());
//				List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);                   				
//				if (addresses.size() > 0) {
//					//for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
//					//	add += addresses.get(0).getAddressLine(i) + "\n";        					 
//					city = addresses.get(0).getLocality();  	// Locality of address, i.e "Mountain View", or null
//					state = addresses.get(0).getAdminArea();  	// Administrative area, i.e. "CA", or null
//					ctry = addresses.get(0).getCountryName();	// Country
//					pc = addresses.get(0).getPostalCode();  	// Postal Code	
//				}				
//				//////////////////////////////////////////////////////////////////////////////////////////////////
			