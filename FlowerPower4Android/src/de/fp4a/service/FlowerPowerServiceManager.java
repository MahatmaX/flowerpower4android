package de.fp4a.service;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import de.fp4a.model.FlowerPower;
import de.fp4a.util.FlowerPowerConstants;

public class FlowerPowerServiceManager implements IFlowerPowerServiceManager
{
	private final int STATE_DEVICE_DISCOVERED		= -1;
	
	private final int STATE_SERVICE_CONNECTED 		= 0;
	private final int STATE_SERVICE_DISCONNECTED 	= 1;
	private final int STATE_SERVICE_FAILED		 	= 2;
	
	private final int STATE_DEVICE_CONNECTED	 	= 3;
	private final int STATE_DEVICE_DISCONNECTED	 	= 4;
	private final int STATE_DEVICE_READY		 	= 5;
	private final int STATE_DATA_AVAILABLE		 	= 6;
	
	
	private List<IFlowerPowerServiceListener> listener;
	private Context context;
	private String deviceAddress;
	
	private static FlowerPowerServiceManager singletonInstance;
	
	private FlowerPowerService service;
	
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName componentName, IBinder serviceBinder)
		{
			service = ((FlowerPowerService.LocalBinder) serviceBinder).getService();
			try
			{
				service.initialize();
			}
			catch(RuntimeException exc)
			{
				Log.e(FlowerPowerConstants.TAG, "Unable to initialize Bluetooth service");
				informListener(STATE_SERVICE_FAILED, exc);
				return;
			}
			
			service.connect(deviceAddress); // Automatically connects to the device upon successful start-up initialization.
			Log.i(FlowerPowerConstants.TAG, "Bluetooth connection requested");
			informListener(STATE_SERVICE_CONNECTED, service);
		}

		public void onServiceDisconnected(ComponentName componentName)
		{
			service = null;
			Log.i(FlowerPowerConstants.TAG, "Bluetooth service disconnected");
			informListener(STATE_SERVICE_DISCONNECTED, null);
		}
	};
	
	private final BroadcastReceiver serviceUpdateReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (FlowerPowerService.CONNECTED.equals(action))
				informListener(STATE_DEVICE_CONNECTED, null);
			else if (FlowerPowerService.DISCONNECTED.equals(action))
				informListener(STATE_DEVICE_DISCONNECTED, null);
			else if (FlowerPowerService.SERVICES_DISCOVERED.equals(action))
				informListener(STATE_DEVICE_READY, service);
			else if (FlowerPowerService.DATA_AVAILABLE.equals(action))
			{
				FlowerPower fp = (FlowerPower)intent.getSerializableExtra(FlowerPowerService.EXTRA_DATA_MODEL);
				informListener(STATE_DATA_AVAILABLE, fp);
			}
			else if (FlowerPowerService.DEVICE_DISCOVERED.equals(action))
			{
				BluetoothDeviceModel device = (BluetoothDeviceModel)intent.getSerializableExtra(FlowerPowerService.EXTRA_DEVICE_MODEL);
				informListener(STATE_DEVICE_DISCOVERED, device);
			}
		}
	};
	
	private FlowerPowerServiceManager(String deviceAddress, Context context)
	{
		this.deviceAddress = deviceAddress;
		this.context = context;
		this.listener = new ArrayList<IFlowerPowerServiceListener>();
		
	}

	public static FlowerPowerServiceManager getInstance(String deviceAddress, Context context)
	{
		if (singletonInstance == null)
			singletonInstance = new FlowerPowerServiceManager(deviceAddress, context);
		return singletonInstance;
	}
	
	public void bind()
	{
		context.bindService(new Intent(context, FlowerPowerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbind()
	{
		context.unbindService(serviceConnection);
		service = null;
	}
	
	public void connect()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FlowerPowerService.CONNECTED);
		intentFilter.addAction(FlowerPowerService.DISCONNECTED);
		intentFilter.addAction(FlowerPowerService.SERVICES_DISCOVERED);
		intentFilter.addAction(FlowerPowerService.DATA_AVAILABLE);

		context.registerReceiver(serviceUpdateReceiver, intentFilter);
		
		if (service != null)
		{
			boolean result = service.connect(deviceAddress);
			Log.d(FlowerPowerConstants.TAG, "Connect request result=" + result);
		}
	}

	/**
	 * Disconnect from the Flower Power.
	 */
	public void disconnect()
	{
		service.disconnect();
	}
	
	/**
	 * Check if a Flower Power is connected.
	 * @return  true, if a Flower Power is currently connected, false otherwise.
	 */
	public boolean isConnected()
	{
		if (service == null)
			return false;
		
		return service.isConnected();
	}
	
	/**
	 * Check if Bluetooth is enabled on the Android device.
	 * @return  true, if Bluetooth is currently enabled, false otherwise.
	 */
	public boolean isEnabled()
	{
		if (service == null)
			return false;
		
		return service.isEnabled();
	}
	
	public void pause()
	{
		context.unregisterReceiver(serviceUpdateReceiver);
	}

	public void enablePersistency(long period, int maxListSize, String storageLocation, String seriesId)
	{
		service.enablePersistency(period, maxListSize, storageLocation, seriesId);
	}

	public void disablePersistency()
	{
		service.disablePersistency();
	}
	
	public void enableAutoConnect(long period)
	{
		service.enableAutoConnect(period);
	}

	public void disableAutoConnect()
	{
		service.disableAutoConnect();
	}
	
	public void addServiceListener(IFlowerPowerServiceListener listener)
	{
		if (!this.listener.contains(listener))
			this.listener.add(listener);
	}

	public void removeServiceListener(IFlowerPowerServiceListener listener)
	{
		this.listener.remove(listener);
	}

	private void informListener(int state, Object extra)
	{
		for (int i=0; i < listener.size(); i++)
		{
			if (state == STATE_DATA_AVAILABLE)
				listener.get(i).dataAvailable((FlowerPower) extra);
			else if (state == STATE_DEVICE_READY)
				listener.get(i).deviceReady((IFlowerPowerDevice) extra);
			else if (state == STATE_DEVICE_CONNECTED)
				listener.get(i).deviceConnected();
			else if (state == STATE_DEVICE_DISCONNECTED)
				listener.get(i).deviceDisconnected();
			else if (state == STATE_DEVICE_DISCOVERED)
				listener.get(i).deviceDiscovered((BluetoothDeviceModel)extra);
			else if (state == STATE_SERVICE_CONNECTED)
				listener.get(i).serviceConnected();
			else if (state == STATE_SERVICE_DISCONNECTED)
				listener.get(i).serviceDisconnected();
			else if (state == STATE_SERVICE_FAILED)
				listener.get(i).serviceFailed((RuntimeException)extra);
		}
	}
	
	/**
	 * Call this upon finalizing. This method disabled auto-connect, disabled persistency, disconnects from the Flower Power and unbinds the service.
	 */
	public void destroy()
	{
		disableAutoConnect();
		disablePersistency();
		disconnect();
		unbind(); 
	}
}
