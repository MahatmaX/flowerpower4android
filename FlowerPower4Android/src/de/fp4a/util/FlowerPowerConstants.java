package de.fp4a.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

public class FlowerPowerConstants
{
	public final static String TAG = "FlowerPower4Android";
	
	public final static String SERVICE_UUID_DEVICE_INFORMATION 			= "0000180a-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_SYSTEM_ID 			= "00002a23-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_MODEL_NR 			= "00002a24-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_SERIAL_NR 			= "00002a25-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_FIRMWARE_REVISION	= "00002a26-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_HARDWARE_REVISION	= "00002a27-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_SOFTWARE_REVISION	= "00002a28-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_MANUFACTURER_NAME	= "00002a29-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID_CERT_DATA			= "00002a2a-0000-1000-8000-00805f9b34fb"; // IEEE 11073-20601 regulatory certification data list
	public final static String CHARACTERISTIC_UUID_PNP_ID				= "00002a50-0000-1000-8000-00805f9b34fb"; 
	
	 
	
	public final static String SERVICE_UUID_SOME_OTHER_SERVICE 			= "0000180f-0000-1000-8000-00805f9b34fb"; // e.g. for battery
	public final static String CHARACTERISTIC_UUID_BATTERY_LEVEL		= "00002a19-0000-1000-8000-00805f9b34fb";
	
	
	
	public final static String SERVICE_UUID_SOME_OTHER_SERVICE_2 		= "39e1fe00-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_FRIENDLY_NAME		= "39e1fe03-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_COLOR	 			= "39e1fe04-84a8-11e2-afba-0002a5d5c51b";
	
	
	
	public final static String SERVICE_UUID_FLOWER_POWER 				= "39e1fa00-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_LIVE_MODE			= "39e1fa06-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_SUNLIGHT 			= "39e1fa01-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_TEMPERATURE 			= "39e1fa04-84a8-11e2-afba-0002a5d5c51b";
	public final static String CHARACTERISTIC_UUID_SOIL_MOISTURE		= "39e1fa05-84a8-11e2-afba-0002a5d5c51b";
	
	
	public static String getCharacteristicName(BluetoothGattCharacteristic characteristic, Context context)
	{
		String uuid = characteristic.getUuid().toString();
		if (uuid.equals(CHARACTERISTIC_UUID_SYSTEM_ID))
			return "System ID";
		else if (uuid.equals(CHARACTERISTIC_UUID_MODEL_NR))
			return "Model Nr.";
		else if (uuid.equals(CHARACTERISTIC_UUID_SERIAL_NR))
			return "Serial Nr";
		else if (uuid.equals(CHARACTERISTIC_UUID_FIRMWARE_REVISION))
			return "Firmware Revision";
		else if (uuid.equals(CHARACTERISTIC_UUID_HARDWARE_REVISION))
			return "Hardware Revision";
		else if (uuid.equals(CHARACTERISTIC_UUID_SOFTWARE_REVISION))
			return "Software Revision";
		else if (uuid.equals(CHARACTERISTIC_UUID_MANUFACTURER_NAME))
			return "Manufacturer Name";
		else if (uuid.equals(CHARACTERISTIC_UUID_CERT_DATA))
			return "Certification Data";
		else if (uuid.equals(CHARACTERISTIC_UUID_PNP_ID))
			return "PNP ID";
		else if (uuid.equals(CHARACTERISTIC_UUID_SUNLIGHT))
			return "Sunlight";
		else if (uuid.equals(CHARACTERISTIC_UUID_TEMPERATURE))
			return "Temperature";
		else if (uuid.equals(CHARACTERISTIC_UUID_SOIL_MOISTURE))
			return "Soil Moisture";
		else if (uuid.equals(CHARACTERISTIC_UUID_COLOR))
			return "Color";
		else if (uuid.equals(CHARACTERISTIC_UUID_FRIENDLY_NAME))
			return "Friendly Name";
		else if (uuid.equals(CHARACTERISTIC_UUID_BATTERY_LEVEL))
			return "Battery Level";
		return null;
	}
}
