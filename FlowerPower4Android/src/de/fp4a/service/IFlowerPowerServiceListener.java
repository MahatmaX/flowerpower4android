package de.fp4a.service;

import de.fp4a.model.FlowerPower;

public interface IFlowerPowerServiceListener
{

	public void serviceConnected();
	public void serviceDisconnected();
	public void serviceFailed(RuntimeException extra);
	
	public void deviceDiscovered(BluetoothDeviceModel extra);
	public void deviceConnected();
	public void deviceDisconnected();
	public void deviceReady(IFlowerPowerDevice device);
	public void dataAvailable(FlowerPower fp);
	
	
}
