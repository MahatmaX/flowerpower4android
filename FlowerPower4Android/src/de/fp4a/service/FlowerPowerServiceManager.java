package de.fp4a.service;

import java.util.ArrayList;
import java.util.List;

import de.fp4a.model.FlowerPower;
import de.fp4a.util.FlowerPowerConstants;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class FlowerPowerServiceManager implements IFlowerPowerServiceManager
{
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
	
	private IFlowerPowerDevice device;
	
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName componentName, IBinder serviceBinder)
		{
			device = ((FlowerPowerService.LocalBinder) serviceBinder).getService();
			if (!((FlowerPowerService)device).initialize())
			{
				Log.e(FlowerPowerConstants.TAG, "Unable to initialize Bluetooth service");
				informListener(STATE_SERVICE_FAILED, null);
				return;
			}
			device.connect(deviceAddress); // Automatically connects to the device upon successful start-up initialization.
			Log.i(FlowerPowerConstants.TAG, "Bluetooth connection requested");
			informListener(STATE_SERVICE_CONNECTED, device);
		}

		public void onServiceDisconnected(ComponentName componentName)
		{
			device = null;
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
				informListener(STATE_DEVICE_READY, device);
			else if (FlowerPowerService.DATA_AVAILABLE.equals(action))
			{
				FlowerPower fp = (FlowerPower)intent.getSerializableExtra(FlowerPowerService.EXTRA_DATA_MODEL);
				informListener(STATE_DATA_AVAILABLE, fp);
			}
		}
	};
	
	public FlowerPowerServiceManager(String deviceAddress, Context context)
	{
		this.deviceAddress = deviceAddress;
		this.context = context;
		this.listener = new ArrayList<IFlowerPowerServiceListener>();
	}

	public void bind()
	{
		context.bindService(new Intent(context, FlowerPowerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbind()
	{
		context.unbindService(serviceConnection);
		device = null;
	}
	
	public void connect()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FlowerPowerService.CONNECTED);
		intentFilter.addAction(FlowerPowerService.DISCONNECTED);
		intentFilter.addAction(FlowerPowerService.SERVICES_DISCOVERED);
		intentFilter.addAction(FlowerPowerService.DATA_AVAILABLE);

		context.registerReceiver(serviceUpdateReceiver, intentFilter);
		
		if (device != null)
		{
			boolean result = device.connect(deviceAddress);
			Log.d(FlowerPowerConstants.TAG, "Connect request result=" + result);
		}
	}

	public void disconnect()
	{
		device.disconnect();
	}
	
	public boolean isConnected()
	{
		if (device == null)
			return false;
		
		return device.isConnected();
	}
	
	public void pause()
	{
		context.unregisterReceiver(serviceUpdateReceiver);
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
			else if (state == STATE_SERVICE_CONNECTED)
				listener.get(i).serviceConnected();
			else if (state == STATE_SERVICE_DISCONNECTED)
				listener.get(i).serviceDisconnected();
			else if (state == STATE_SERVICE_FAILED)
				listener.get(i).serviceFailed();
		}
		
	}
}
