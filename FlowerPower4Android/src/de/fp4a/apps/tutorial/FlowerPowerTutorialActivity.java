package de.fp4a.apps.tutorial;

import de.fp4a.R;
import de.fp4a.util.FlowerPowerConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FlowerPowerTutorialActivity extends Activity
{

	/** Called once the Activity is initially created */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flowerpower_main);

		Intent intent = getIntent();
		final String deviceName = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_NAME);
		final String deviceAddress = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_ADDRESS);
		
		
	}

	/** Called once the Activity is visible after having been paused */
	protected void onResume()
	{
		super.onResume();
	}

	/** Called once the Activity is no longer visible (e.g. because another Activity is now in the foreground) */
	protected void onPause()
	{
		super.onPause();
	}

	/** Called once the Activity is finally being destroyed */
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
