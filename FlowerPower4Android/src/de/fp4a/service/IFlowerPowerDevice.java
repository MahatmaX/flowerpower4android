package de.fp4a.service;

public interface IFlowerPowerDevice
{

	public void readSystemId();
	public void readModelNr();
	public void readSerialNr();
	public void readFirmwareRevision();
	public void readHardwareRevision();
	public void readSoftwareRevision();
	public void readManufacturerName();
	public void readCertData();
	public void readPnpId();
	public void readFriendlyName();
	public void readColor();
	
	public void readBatteryLevel();
	public void readTemperature();
	public void readSoilMoisture();
	public void readSunlight();
	
	public void notifyTemperature(boolean enable, long period);
	public void notifySoilMoisture(boolean enable, long period);
	public void notifySunlight(boolean enable, long period);
	public void notifyBatteryLevel(boolean enable, long period);
	
	public boolean isNotifyTemperature();
	public boolean isNotifySunlight();
	public boolean isNotifySoilMoisture();
	public boolean isNotifyBatteryLevel();
	
}
