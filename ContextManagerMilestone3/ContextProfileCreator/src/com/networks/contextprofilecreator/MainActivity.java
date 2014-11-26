package com.networks.contextprofilecreator;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.alternativevision.gpx.GPXParser;
import org.alternativevision.gpx.beans.GPX;


import android.app.Activity;
import android.content.Context;
//import android.app.AlertDialog;
//import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;
import android.location.Location;
//import android.widget.Toast;
//import android.location.LocationManager;
import android.location.LocationManager;
//import java.io.FileInputStream;

public class MainActivity extends Activity {	
	
//	ListView lv;
	private ContextManager objContextDatahandler = null;
	int delayupdates=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		lv = (ListView) findViewById(R.id.listView1);
    	
		setContentView(R.layout.activity_mobility_pattern);
		objContextDatahandler = new ContextManager((LocationManager)this.getSystemService(Context.LOCATION_SERVICE),this.getApplicationContext());
		
		Location loc = new Location("XYZ");	
		objContextDatahandler.updateContextInfo();
		objContextDatahandler.setTrackingLoc(loc);
//		objContextDatahandler.printUpdates();
//		objContextDatahandler.GPXLocationsString();
		
//		GPX gpx;
//		try
//		{
//			GPXParser p = new GPXParser();
//			FileInputStream in = new FileInputStream("C:\\virtua's stuff\\HybridComputationOffloading\\ContextManagerMilestone3\\LocationGpx\\CloughToKlaus.gpx");
//			gpx = p.parseGPX(in);
//		}
//		catch(Exception ex)
//		{
//			
//		}

	
		final Handler handler = new Handler();
		final Runnable r = new Runnable()
		{
		    public void run() 
		    {
		    	Location loc = new Location("XYZ");	
				objContextDatahandler.updateContextInfo();
				objContextDatahandler.setTrackingLoc(loc);
//				objContextDatahandler.printUpdates(MainActivity.this);
		    	handler.postDelayed(this,delayupdates*60*1000);// This runs task every 2 minutes

		    }
		};
		handler.postDelayed(r,delayupdates*60*1000); 
		
	}

	public void updateBtn(View v) {
//		ArrayList<String> list = new ArrayList<String>();
		try {
			Location loc = new Location("XYZ");
			objContextDatahandler.setTrackingLoc(loc);
			objContextDatahandler.printUpdates(this);

			Toast.makeText(getBaseContext(), "Got update",Toast.LENGTH_SHORT)
					.show();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), "Caught", Toast.LENGTH_SHORT)
					.show();
		}

		// display info
//		lv.setAdapter(new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, list));

//		lv.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View v, int pos,
//					long id) {
//				
//				// do nothing
//			}
//		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mobility_pattern, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
