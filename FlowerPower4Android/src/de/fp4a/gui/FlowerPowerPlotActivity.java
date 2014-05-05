package de.fp4a.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import de.fp4a.R;
import de.fp4a.persistency.PersistencyManager;

public class FlowerPowerPlotActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flowerpower_plot);
		
		FlowerPowerPlotFragment fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_temperature);
		fragment.init(PersistencyManager.TIMESERIES_ID_TEMPERATURE, "Temperature", -10, 50);
		
		fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_sunlight);
		fragment.init(PersistencyManager.TIMESERIES_ID_SUNLIGHT, "Sunlight", 0, 10);
		
		fragment = (FlowerPowerPlotFragment) getFragmentManager().findFragmentById(R.id.frag_soilmoisture);
		fragment.init(PersistencyManager.TIMESERIES_ID_SOILMOISTURE, "Soil Moisture", 0, 100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.flowerpower_graph, menu);
		return true;
	}

}
