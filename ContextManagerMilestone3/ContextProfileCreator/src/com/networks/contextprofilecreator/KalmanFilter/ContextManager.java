package com.networks.contextprofilecreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.Runtime;

public class ContextManager extends Activity {//implements SensorEventListener{
	private LocationManager locationManager;
	private LocationListener locationListener;
	private String locationProvider = LocationManager.GPS_PROVIDER;
	private Location lastKnownLocation;
	private HistoricalContext objHistory = new HistoricalContext();
	private KalmanFilterMobility objKalmanFilter = new KalmanFilterMobility();
	private Location trackingLoc;
	private String uniqueID = "XYZ";		//Placeholder
//	private SensorManager objSensorManager;
//	private Sensor objSensor;
	private double accU = 1;
	private String filename = "Context_History";
	
	public Location getTrackingLoc()
	{
		return trackingLoc;
	}
	public void setTrackingLoc(Location lat)
	{
		trackingLoc = lat;
	}
	
	public ContextManager()
	{
		//Load Data from file -- To Be Implemented
		checkDataBackup();
		//Sensor to get acceleration data
//		objSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		if (objSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
//		    objSensor = objSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//		  }
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
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
		updateContextInfo();
	}
	
	private void checkDataBackup()
	{
		String strContextData = "";
		try
		{
			FileInputStream is = this.openFileInput(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is!=null) {	
				while ((strContextData = reader.readLine()) != null) {	
					ContextInfo objContextInfo = new ContextInfo(strContextData);
					objHistory.appendContextdata(objContextInfo);
					long timeDiff = objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime();
					objKalmanFilter.Update(objContextInfo.getContextLocation(),timeDiff);
				}				
			}		
			is.close();	
		}
		catch(Exception ex)
		{
			
		}
	}
	
//	@Override
//	  public final void onSensorChanged(SensorEvent event) {
//	    accU = event.values[0];
//	  }
//	
//	@Override
//	  protected void onResume() {
//	    super.onResume();
//	    objSensorManager.registerListener(this, objSensor, SensorManager.SENSOR_DELAY_NORMAL);
//	  }
//
//	  @Override
//	  protected void onPause() {
//	    super.onPause();
//	    objSensorManager.unregisterListener(this);
//	  }
	
	private void getLocUpdates(ContextInfo objContextInfo)
	{
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
		Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
		boolean loc = isBetterLocation(currentLocation, lastKnownLocation);
		if(loc)
		{
			lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		}
//		objContextInfo.setContextLocation(lastKnownLocation);
		objContextInfo.setContextAcc(accU);
		locationManager.removeUpdates(locationListener);
	}
	
	public void printUpdates()
	{
		ContextInfo objContextInfo = objHistory.getLastContextdata();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Context Infromation");
//		alertDialog.setMessage("Context Distance: " + objContextInfo.getContextDistanceFrom());
		alertDialog.setMessage("Context Latitude: " + objContextInfo.getContextLatitude());
		alertDialog.setMessage("Context Longitude: " + objContextInfo.getContextLongitude());
		alertDialog.setMessage("Context CPU Usage: " + objContextInfo.getContextCPUUsage());
		long timeDiff = objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime();
		alertDialog.setMessage("Context Predicted Location: " + getPredictedLoc(timeDiff));
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
		ContextInfo objContextInfo = new ContextInfo();
		this.getLocUpdates(objContextInfo);
		this.getCPUusage(objContextInfo);
		objContextInfo.setStartTime();
		objHistory.appendContextdata(objContextInfo);
		long timeDiff = objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime();
		objKalmanFilter.Update(objContextInfo.getContextLocation(),timeDiff);
	}
	
	private void getCPUusage(ContextInfo objContextInfo)
	{
		// -m 10, how many entries you want, -d 1, delay by how much, -n 1,
		// number of iterations
		ArrayList<String> list = new ArrayList<String>();
		try{
			Process p = Runtime.getRuntime().exec("top -m 15 -d 2 -n 2");
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				list.add(line);
				line = reader.readLine();
			}
			p.waitFor();
		}
		catch(Exception ex)
		{
			
		}
		objContextInfo.setContextCPUUsage(list.toString());
		
	}
	
	public void saveStateData()
	{
		//To be implemented --- save data to file
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  for(int i=0; i < objHistory.getContextlength(); i ++)
		  {
			  ContextInfo objContextInfo = objHistory.getContextdata(i);
			  String contextData = objContextInfo.getStartTime() +"," 
					  + objContextInfo.getContextAcc() +"," + objContextInfo.getContextLatitude() +","
					  + objContextInfo.getContextLongitude() +"," 
					  + objContextInfo.getContextSpeed() +","
					  + objContextInfo.getContextCPUUsage();
			  outputStream.write(contextData.getBytes());
		  }
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	private Location getPredictedLoc(double time)
	{
		return objKalmanFilter.predictTime(time,uniqueID);		
	}
}
