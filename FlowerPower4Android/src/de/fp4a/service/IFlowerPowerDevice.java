package de.fp4a.service;

public interface IFlowerPowerDevice
{

	public boolean connect(String deviceAddress);
	public void disconnect();
	public boolean isConnected();
	
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
	
	public void notifyTemperature(boolean enable);
	public void notifySoilMoisture(boolean enable);
	public void notifySunlight(boolean enable);
}
