package com.networks.contextprofilecreator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
//import android.widget.Toast;

public class MobilityPattern extends Activity {	
	private ContextManager objContextDatahandler = new ContextManager();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobility_pattern);
		Location loc = new Location("XYZ");		
		objContextDatahandler.setTrackingLoc(loc);
		objContextDatahandler.printUpdates();
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
	
	@Override
	public void onDestroy()
	{
		//To be implemented save states to file for future use
//		objContextDatahandler.saveStateData();
	}
}

