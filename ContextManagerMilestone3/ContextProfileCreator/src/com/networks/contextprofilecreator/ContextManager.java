package com.networks.contextprofilecreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.Runtime;

public class ContextManager {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private String locationProvider = LocationManager.GPS_PROVIDER;
	private Location lastKnownLocation;
	private ContextInfo objContextInfo = new ContextInfo();
	private Location trackingLoc;
	
	public Location getTrackingLoc()
	{
		return trackingLoc;
	}
	public void setTrackingLoc(Location lat)
	{
		trackingLoc = lat;
	}
	
	public ContextManager(LocationManager tempLoc)
	{
		// Acquire a reference to the system Location Manager
		locationManager = tempLoc;
		//
		//	// Define a listener that responds to location updates
		locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	      //makeUseOfNewLocation(location);
	    }
	
	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	    public void onProviderEnabled(String provider) {}
	
	    public void onProviderDisabled(String provider) {}
		};

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationProvider = LocationManager.NETWORK_PROVIDER;
		}
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
		lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
	}
	
	private void getLocUpdates()
	{
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
		Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
		boolean loc = isBetterLocation(currentLocation, lastKnownLocation);
		if(loc)
		{
			lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		}
//		objContextInfo.setContextDistanceFrom(lastKnownLocation.distanceTo(trackingLoc));
		objContextInfo.setContextLatitude(lastKnownLocation.getLatitude());
		objContextInfo.setContextLongitude(lastKnownLocation.getLongitude());
		locationManager.removeUpdates(locationListener);
	}
	
	public void printUpdates(Context objTemp)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(objTemp);
		alertDialog.setTitle("Context Infromation");
//		alertDialog.setMessage("Context Distance: " + objContextInfo.getContextDistanceFrom());
//		alertDialog.setMessage("Context Latitude: " + objContextInfo.getContextLatitude());
//		alertDialog.setMessage("Context Longitude: " + objContextInfo.getContextLongitude());
		alertDialog.setMessage("Context CPU Usage: " + objContextInfo.getContextCPUUsage());
		alertDialog.setPositiveButton("Ok", null);
		alertDialog.show();
	}
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }
//
//	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;
//
//	    // If it's been more than two minutes since the current location, use the new location
//	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }
//
//	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());
//
//	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	public void updateContextInfo()
	{
		this.getLocUpdates();
		this.getCPUusage();
	}
	
	private void getCPUusage()
	{
		// -m 10, how many entries you want, -d 1, delay by how much, -n 1,
		// number of iterations
		ArrayList<String> list = new ArrayList<String>();
		String line="Salem";
//		String UserCPU = "";
		int UserCPUperc = 0;
		int SystemCPUperc=0;
		try{
			Process p = Runtime.getRuntime().exec("top -m 15 -d 2 -n 2");
			int i=0;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
//			line = reader.readLine();
			while ((i<4)) {
//				list.add(line);
				line = reader.readLine();
				i++;
			}
			int UserCPUstart = line.indexOf(" ");
			int UserCPUend = line.indexOf("%");
			String UserCPU = line.substring(UserCPUstart + 1,UserCPUend);
			try {
			    UserCPUperc = Integer.parseInt(UserCPU);
			} catch(NumberFormatException nfe) {
			   System.out.println("Could not parse " + nfe);
			}
			
			String temp=line.substring(UserCPUend + 4);
			int SystemCPUstart = temp.indexOf(" ");
			int SystemCPUend = temp.indexOf("%");
			String SystemCPU = temp.substring(SystemCPUstart + 1,SystemCPUend);
			try {
			    SystemCPUperc = Integer.parseInt(SystemCPU);
			} catch(NumberFormatException nfe) {
			   System.out.println("Could not parse " + nfe);
			}
			
			p.waitFor();
		}
		catch(Exception ex)
		{
			list.add("Crashed");
//			list = "Crashed";
		}
//		objContextInfo.setContextCPUUsage(list.toString());
		objContextInfo.setContextCPUUsage(SystemCPUperc+UserCPUperc);
	}
}
