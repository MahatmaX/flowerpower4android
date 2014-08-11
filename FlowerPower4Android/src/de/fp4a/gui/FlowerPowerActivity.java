/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fp4a.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import de.fp4a.R;
import de.fp4a.model.FlowerPower;
import de.fp4a.service.BluetoothDeviceModel;
import de.fp4a.service.FlowerPowerServiceManager;
import de.fp4a.service.IFlowerPowerDevice;
import de.fp4a.service.IFlowerPowerServiceListener;
import de.fp4a.service.IFlowerPowerServiceManager;
import de.fp4a.util.FlowerPowerConstants;
import de.fp4a.util.Util;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class FlowerPowerActivity extends Activity
{
	private IFlowerPowerServiceManager serviceManager;
	private IFlowerPowerDevice device;
	
	private void updateUI(FlowerPower fp)
	{
		((TextView)findViewById(R.id.tv_system_id)).setText(fp.getMetadata().getSystemId()+"");
		((TextView)findViewById(R.id.tv_model_nr)).setText(fp.getMetadata().getModelNr()+"");
		((TextView)findViewById(R.id.tv_serial_nr)).setText(fp.getMetadata().getSerialNr()+"");
		((TextView)findViewById(R.id.tv_firmware_rev)).setText(fp.getMetadata().getFirmwareRevision()+"");
		((TextView)findViewById(R.id.tv_hardware_rev)).setText(fp.getMetadata().getHardwareRevision()+"");
		((TextView)findViewById(R.id.tv_software_rev)).setText(fp.getMetadata().getSoftwareRevision()+"");
		((TextView)findViewById(R.id.tv_manufacturer_name)).setText(fp.getMetadata().getManufacturerName()+"");
		((TextView)findViewById(R.id.tv_cert_data)).setText(fp.getMetadata().getCertData()+"");
		((TextView)findViewById(R.id.tv_pnp_id)).setText(fp.getMetadata().getPnpId()+"");
		((TextView)findViewById(R.id.tv_friendly_name)).setText(fp.getMetadata().getFriendlyName()+"");
		((TextView)findViewById(R.id.tv_color)).setText(fp.getMetadata().getColor()+"");
		
		((TextView)findViewById(R.id.tv_battery_level)).setText(fp.getBatteryLevel()+"");
		((TextView)findViewById(R.id.tv_battery_level_timestamp)).setText(Util.getHumanReadableTimestamp(fp.getBatteryLevelTimestamp(), false)+"");
		
		((TextView)findViewById(R.id.tv_temperature)).setText(fp.getTemperature()+"");
		((TextView)findViewById(R.id.tv_temperature_timestamp)).setText(Util.getHumanReadableTimestamp(fp.getTemperatureTimestamp(), false)+"");
		
		((TextView)findViewById(R.id.tv_sunlight)).setText(fp.getSunlight()+"");
		((TextView)findViewById(R.id.tv_sunlight_timestamp)).setText(Util.getHumanReadableTimestamp(fp.getSunlightTimestamp(), false)+"");
		
		((TextView)findViewById(R.id.tv_soil_moisture)).setText(fp.getSoilMoisture()+"");
		((TextView)findViewById(R.id.tv_soil_moisture_timestamp)).setText(Util.getHumanReadableTimestamp(fp.getSoilMoistureTimestamp(), false)+"");
	}
	
	@Override
	/** Called once the Activity is initially created */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(FlowerPowerConstants.TAG, "Activity.onCreate()");
		
		setContentView(R.layout.flowerpower_main);

		Intent intent = getIntent();
		final String deviceName = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_NAME);
		final String deviceAddress = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_ADDRESS);

		getActionBar().setTitle(deviceName);
		final ImageButton graphButton = (ImageButton)findViewById(R.id.btn_graph);
		graphButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				Intent i = new Intent(FlowerPowerActivity.this, FlowerPowerPlotActivity.class);
				i.putExtra(FlowerPowerConstants.EXTRAS_DEVICE_ADDRESS, deviceAddress);
				i.putExtra(FlowerPowerConstants.EXTRAS_DEVICE_NAME, deviceName);
				startActivity(i);
			}
		});
		
		final CheckBox checkBox = (CheckBox)findViewById(R.id.checkbox_notifications);
		checkBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				Log.i(FlowerPowerConstants.TAG, "Activity.onClick() Notifications " + (checkBox.isChecked() ? "enabled" : "disabled") + ", device connected ? " + serviceManager.isConnected());
				
				device.notifyTemperature(checkBox.isChecked());
				device.notifySunlight(checkBox.isChecked());
				device.notifySoilMoisture(checkBox.isChecked());
				device.notifyBatteryLevel(checkBox.isChecked());
				
				// once notifications are enabled, the persistency manager is also initialized in order to store all those values
				serviceManager.enablePersistency(5000, 1000, FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL, "flowerpower4android");
				
				// and check once in a minute if the flower power is still connected and try to reconnect if not 
				serviceManager.enableReconnect(60000);
			}
		});
		
		
		serviceManager = FlowerPowerServiceManager.getInstance(deviceAddress, this);
		
		IFlowerPowerServiceListener serviceListener = new IFlowerPowerServiceListener() {
			
			public void deviceDiscovered(BluetoothDeviceModel device)
			{
				Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() device discovered: " + device);
			}
			
			public void deviceConnected()
			{
				Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() device connected");
				invalidateOptionsMenu();
				
				serviceManager.enableReconnect(1000 * 60 * 10); // 10 mins
			}
			
			public void deviceDisconnected()
			{
				Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() device disconnected");
				invalidateOptionsMenu();
				
				((CheckBox)findViewById(R.id.checkbox_notifications)).setEnabled(false);
				FlowerPowerActivity.this.device = null;
				
//				serviceManager.disconnect();
			}
			
			public void deviceReady(IFlowerPowerDevice device)
			{
				Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() device ready");
				((CheckBox)findViewById(R.id.checkbox_notifications)).setEnabled(true);
				FlowerPowerActivity.this.device = device;
				
				device.readSystemId();
				device.readModelNr();
				device.readSerialNr();
				device.readFirmwareRevision();
				device.readHardwareRevision();
				device.readSoftwareRevision();
				device.readManufacturerName();
				device.readCertData();
				device.readPnpId(); 
				device.readFriendlyName();
				device.readColor();
				
				device.readBatteryLevel();
				device.readTemperature();
				device.readSunlight(); 
				device.readSoilMoisture();
			}
			
			public void dataAvailable(FlowerPower fp)
			{
				updateUI(fp);
			}
			
			public void serviceConnected() { Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() service connected"); /* does not neccessarily need to be handled */ }
			
			public void serviceDisconnected() { Log.i(FlowerPowerConstants.TAG, "Activity.serviceListener() service disconnected");/* does not neccessarily need to be handled */ }				
			
			public void serviceFailed(RuntimeException exc)
			{
				exc.printStackTrace();
				Log.e(FlowerPowerConstants.TAG, "Activity.serviceListener() service failed -> finish");
				finish();
			}
		};
		serviceManager.addServiceListener(serviceListener);
		
		Log.i(FlowerPowerConstants.TAG, "Activity.onCreate() bind serviceManager");
		serviceManager.bind();
	}

	@Override
	/** Called once the Activity is visible after having been paused */
	protected void onResume()
	{
		super.onResume();
		Log.i(FlowerPowerConstants.TAG, "Activity.onResume() connect serviceManager");
		serviceManager.connect();
	}

	@Override
	/** Called once the Activity is no longer visible (e.g. because another Activity is now in the foreground) */
	protected void onPause()
	{
		super.onPause();
		Log.i(FlowerPowerConstants.TAG, "Activity.onPause() pause serviceManager");
//		serviceManager.pause(); // serviceManager will not receive further updates
	}

	@Override
	/** Called once the Activity is finally being destroyed */
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i(FlowerPowerConstants.TAG, "Activity.onDestroy() unbind serviceManager");
		
		serviceManager.destroy(); // disconnect, shutdown service, free all resources.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.flowerpower, menu);
		if (serviceManager.isConnected())
		{
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		}
		else
		{
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_connect:
				serviceManager.connect();
				return true;
			case R.id.menu_disconnect:
				serviceManager.disconnect();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
