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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import de.fp4a.R;
import de.fp4a.model.FlowerPower;
import de.fp4a.service.FlowerPowerServiceManager;
import de.fp4a.service.IFlowerPowerDevice;
import de.fp4a.service.IFlowerPowerServiceListener;
import de.fp4a.service.IFlowerPowerServiceManager;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class FlowerPowerActivity extends Activity
{
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private IFlowerPowerServiceManager serviceManager;
	
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
		((TextView)findViewById(R.id.tv_temperature)).setText(fp.getTemperature()+"");
		((TextView)findViewById(R.id.tv_sunlight)).setText(fp.getSunlight()+"");
		((TextView)findViewById(R.id.tv_soil_moisture)).setText(fp.getSoilMoisture()+"");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flowerpower_main);

		final Intent intent = getIntent();
		String deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		String deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		getActionBar().setTitle(deviceName);
		serviceManager = new FlowerPowerServiceManager(deviceAddress, this);
		IFlowerPowerServiceListener serviceListener = new IFlowerPowerServiceListener() {
			
			public void deviceConnected()
			{
				invalidateOptionsMenu();
			}
			
			public void deviceDisconnected()
			{
				invalidateOptionsMenu();
			}
			
			public void deviceReady(IFlowerPowerDevice device)
			{
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
			
			public void serviceConnected() { /* does not neccessarily need to be handled */ }
			
			public void serviceDisconnected() { /* does not neccessarily need to be handled */ }				
			
			public void serviceFailed()
			{
				finish();
			}
		};
		serviceManager.addServiceListener(serviceListener);
		serviceManager.bind();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		serviceManager.connect();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		serviceManager.pause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		serviceManager.unbind();
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
