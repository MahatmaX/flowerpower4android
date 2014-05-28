package de.fp4a.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.fp4a.R;
import de.fp4a.persistency.PersistencyManager;
import de.fp4a.util.FlowerPowerConstants;

public class FlowerPowerPlotActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flowerpower_plot);
		
		Intent intent = getIntent();
		final String deviceName = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_NAME);
		getActionBar().setTitle(deviceName);
		
		FlowerPowerPlotFragment fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_temperature);
		fragment.init(PersistencyManager.TIMESERIES_TYPE_TEMPERATURE, "flowerpower4android", 1000, FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL, 
				"Temperature", Color.RED, Color.WHITE, 200, Color.parseColor("#800000"));
		 
		fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_sunlight);
		fragment.init(PersistencyManager.TIMESERIES_TYPE_SUNLIGHT, "flowerpower4android", 1000, FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL,
				"Sunlight", Color.YELLOW, Color.WHITE, 200, Color.parseColor("#FFBA00"));
		
		fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_soilmoisture);
		fragment.init(PersistencyManager.TIMESERIES_TYPE_SOILMOISTURE, "flowerpower4android", 1000, FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL,
				"Soil Moisture", Color.GREEN, Color.WHITE, 200, Color.parseColor("#00C000"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.flowerpower_graph, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_clear:
				((FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_temperature)).clear();
				((FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_sunlight)).clear();
				((FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_soilmoisture)).clear();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
