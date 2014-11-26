package com.networks.contextprofilecreator;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.AlertDialog;
import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.DocumentsContract.Document;
import android.provider.Settings.Secure;
import android.renderscript.Element;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.util.ArrayList;
import java.lang.Runtime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
	private Context fileIO;
	
	public Location getTrackingLoc()
	{
		return trackingLoc;
	}
	public void setTrackingLoc(Location lat)
	{
		trackingLoc = lat;
	}
	
	public ContextManager(LocationManager objLoc, Context objTemp)
	{
		uniqueID = Secure.getString(objTemp.getContentResolver(), Secure.ANDROID_ID);
		fileIO = objTemp;
		//Load Data from file -- To Be Implemented
		checkDataBackup();
		//Sensor to get acceleration data
//		objSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		if (objSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
//		    objSensor = objSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//		  }
		// Acquire a reference to the system Location Manager
		locationManager = objLoc;
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
		objKalmanFilter.debugCntxt = fileIO;
		objKalmanFilter.debugMode = true;
		objKalmanFilter.debugFileMode = Context.MODE_APPEND;
		updateContextInfo();
	}
	

//	public String GPXLocationsString() {
//		  String path = "D:\\Eclipse Juno\\WorkspaceJuno\\LocationGpx\\CloughToKlaus.gpx";
//		   
//		  String info = "";
//		   
//		  File gpxFile = new File(path);
//		  info += gpxFile.getPath() +"\n\n";
//		   
//		 
//		  List<Location> gpxList = decodeGPX(gpxFile);
//		 
//		  for(int i = 0; i < gpxList.size(); i++){
//		   info += ((Location)gpxList.get(i)).getLatitude() 
//		     + " : "
//		     + ((Location)gpxList.get(i)).getLongitude() + "\n" ;
//		  }
//		  return info;
//	}
//		  
//	private List<Location> decodeGPX(File file){
//		  List<Location> list = new ArrayList<Location>();
//		 
//		  DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//		  try {
//		   DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//		   FileInputStream fileInputStream = new FileInputStream(file);
//		   org.w3c.dom.Document document = documentBuilder.parse(fileInputStream);
//		   org.w3c.dom.Element elementRoot = document.getDocumentElement();
//		    
//		   NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
//		 
//		   for(int i = 0; i < nodelist_trkpt.getLength(); i++){
//		     
//		    Node node = nodelist_trkpt.item(i);
//		    NamedNodeMap attributes = node.getAttributes();
//		     
//		    String newLatitude = attributes.getNamedItem("lat").getTextContent();
//		    Double newLatitude_double = Double.parseDouble(newLatitude);
//		     
//		    String newLongitude = attributes.getNamedItem("lon").getTextContent();
//		    Double newLongitude_double = Double.parseDouble(newLongitude);
//		     
//		    String newLocationName = newLatitude + ":" + newLongitude;
//		    Location newLocation = new Location(newLocationName);
//		    newLocation.setLatitude(newLatitude_double);
//		    newLocation.setLongitude(newLongitude_double);
//		     
//		    list.add(newLocation);
//		 
//		   }
//		    
//		   fileInputStream.close();
//		    
//		  } catch (ParserConfigurationException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		  } catch (FileNotFoundException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		  } catch (SAXException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		  } catch (IOException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		  }
//		   
//		  return list;
//	}
	
		 ///////////////////:
	private void checkDataBackup()
	{
		String strContextData = "";
		try
		{
			File tempFile = new File(filename);
			if(tempFile.exists())
			{
				FileInputStream is = this.openFileInput(filename);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				if (is!=null) {	
					while ((strContextData = reader.readLine()) != null) {	
						ContextInfo objContextInfo = new ContextInfo(strContextData);
						double timeDiff = (objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime())*0.001;
						objHistory.appendContextdata(objContextInfo);
						objKalmanFilter.Update(objContextInfo.getContextLocation(),timeDiff);
					}				
				}		
				is.close();	
			}
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
		objContextInfo.setContextLocation(lastKnownLocation);
		objContextInfo.setContextAcc(accU);
		locationManager.removeUpdates(locationListener);
	}
	
	
	public void printUpdates(Context objTemp)
	{
		
		ContextInfo objCurrContextInfo = objHistory.getLastContextdata();
		double CurrLong=objCurrContextInfo.getContextLongitude();
		double CurrLat=objCurrContextInfo.getContextLatitude();
//		uniqueID = Secure.getString(objTemp.getContentResolver(), Secure.ANDROID_ID);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(objTemp);
		alertDialog.setTitle("Context Infromation");
////	alertDialog.setMessage("Context Distance: " + objContextInfo.getContextDistanceFrom());
//		alertDialog.setMessage("Context Latitude: " + objContextInfo.getContextLatitude());
//		alertDialog.setMessage("Context Longitude: " + objContextInfo.getContextLongitude());
//		alertDialog.setMessage("\n Context CPU Usage: " + objContextInfo.getContextCPUUsage());
				
		ContextInfo objContextInfo = new ContextInfo();
		double timeDiff = (objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime())*0.001;
		objContextInfo.setMngrID(uniqueID);
		objContextInfo.setContextAcc(objHistory.getLastContextdata().getContextAcc());
		objContextInfo.setContextCPUUsage(objHistory.getLastContextdata().getContextCPUUsage());
		objContextInfo.setStartTime();
		Location temploc = objKalmanFilter.predictTime(timeDiff,objContextInfo.getMngrID());	
		objContextInfo.setContextLocation(temploc);
		Location temploc1 = objKalmanFilter.predictTime(timeDiff*2,objContextInfo.getMngrID());	
		objContextInfo.setContextLocation(temploc1);
		Location temploc2 = objKalmanFilter.predictTime(timeDiff*3,objContextInfo.getMngrID());	
		objContextInfo.setContextLocation(temploc2);
		Location temploc3 = objKalmanFilter.predictTime(timeDiff*4,objContextInfo.getMngrID());	
		objContextInfo.setContextLocation(temploc3);
		
		alertDialog.setMessage("Context Longitude: "+objContextInfo.getMngrID());// + objContextInfo.getContextLongitude()+"Context Latitude: " + objContextInfo.getContextLatitude()
//				 "Context Longitude0: " + temploc.getLongitude()+"Context Latitude0: " + temploc.getLatitude()
//				+ "Context Longitude1: " + temploc1.getLongitude()+"Context Latitude1: " + temploc1.getLatitude()
//				+ "Context Longitude2: " + temploc2.getLongitude()+"Context Latitude2: " + temploc2.getLatitude()
//				+ "Context Longitude3: " + temploc3.getLongitude()+"Context Latitude3: " + temploc3.getLatitude());
//		
		
//		alertDialog.setMessage(uniqueID + "\n Context CPU Usage: " + objContextInfo.getContextCPUUsage()
//				+"\n Context Longitude: " + objContextInfo.getContextLongitude()+ "\n Context Latitude: " + objContextInfo.getContextLatitude()
//				+ "\n Size" + objHistory.getContextlength()+ "\n sizzzz" + objContextInfo.getContextLocSize());// +"\n Ahawa \n" + salem());

		//		long timeDiff = objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime();
//		alertDialog.setMessage("Context Predicted Location: " + getPredictedLoc(timeDiff));
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
		double timeDiff = 1;
		ContextInfo objContextInfo = new ContextInfo();
		this.getLocUpdates(objContextInfo);
		this.getCPUusage(objContextInfo);
		objContextInfo.setStartTime();
		if(objHistory.getContextlength() > 0)
		{
			timeDiff = (objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime())*0.001;
		}
		objHistory.appendContextdata(objContextInfo);
		objKalmanFilter.Update(objContextInfo.getContextLocation(),timeDiff);
	}
	
	private void getCPUusage(ContextInfo objContextInfo)
	{
		// -m 10, how many entries you want, -d 1, delay by how much, -n 1,
		// number of iterations
		//ArrayList<String> list = new ArrayList<String>();
		String line="";
		int UserCPUperc = 0;
		int SystemCPUperc=0;
		try{
			Process p = Runtime.getRuntime().exec("top -m 10 -d 2 -n 2");
			int i=0;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((i<4)) {
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
//			   System.out.println("Could not parse " + nfe);
			}
			
			p.waitFor();
		}
		catch(Exception ex)
		{
//			list.add("Crashed");
//			list = "Crashed";
		}
		String temp = "" + (SystemCPUperc+UserCPUperc);
		objContextInfo.setContextCPUUsage(temp);
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
					  + objContextInfo.getContextCPUUsage() +"," + objContextInfo.getContextAcc() +",";
			  for(int j = 0; j < objContextInfo.getContextLocSize(); j++)
			  {
				  if(j+1 != objContextInfo.getContextLocSize())
				  {
					  contextData = contextData  + objContextInfo.getContextLatitude() +"|"
								  + objContextInfo.getContextLongitude() +"|" 
								  + objContextInfo.getContextSpeed() + ",";
				  }
				  else
				  {
					  contextData = contextData + objContextInfo.getContextLatitude() +"|"
							  + objContextInfo.getContextLongitude() +"|" 
							  + objContextInfo.getContextSpeed();
				  }
			  }
			  outputStream.write(contextData.getBytes());
		  }
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	
	public ContextInfo getContextInfo()
	{
//		uniqueID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID); 
		ContextInfo objContextInfo = new ContextInfo();
		double timeDiff = (objContextInfo.returnCurrentTime() - objHistory.getLastContextdata().getStartTime())*0.001;
		objContextInfo.setMngrID(uniqueID);
		objContextInfo.setContextAcc(objHistory.getLastContextdata().getContextAcc());
		objContextInfo.setContextCPUUsage(objHistory.getLastContextdata().getContextCPUUsage());
		objContextInfo.setStartTime();
		Location temploc = objKalmanFilter.predictTime(timeDiff,uniqueID + 1);	
		objContextInfo.setContextLocation(temploc);
		Location temploc1 = objKalmanFilter.predictTime(timeDiff*2,uniqueID + 2);	
		objContextInfo.setContextLocation(temploc1);
		Location temploc2 = objKalmanFilter.predictTime(timeDiff*3,uniqueID + 3);	
		objContextInfo.setContextLocation(temploc2);
		Location temploc3 = objKalmanFilter.predictTime(timeDiff*4,uniqueID + 4);	
		objContextInfo.setContextLocation(temploc3);
		return objContextInfo;
		
	}
	
	public Boolean CompareContextInfo(ContextInfo objPeerContextInfo)
	{
		Boolean bResult = false;
		double initDist = 0;
		ContextInfo objInitiatorContextInfo = this.getContextInfo();
		for(int i = 0; i <4; i ++)
		{
			Location objPeerLoc = objPeerContextInfo.getContextLocation(i);
			Location objInitLoc = objInitiatorContextInfo.getContextLocation(i);
			double dist = objPeerLoc.distanceTo(objInitLoc);
			if( i != 0)
			{
				if(dist > initDist)
				{
					bResult = false;
				}
				else
				{
					bResult = true;
				}
			}
			else
			{
				initDist = dist;
			}
		}
		return bResult;
	}
}
