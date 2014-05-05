package de.fp4a.service;

import java.io.Serializable;

public class BluetoothDeviceModel implements Serializable
{

	private String name;
	private String address;
	private int rssi;
	private byte[] scanRecord;
	
	public BluetoothDeviceModel(String name, String address, int rssi, byte[] scanRecord)
	{
		setName(name);
		setAddress(address);
		setRssi(rssi);
		setScanRecord(scanRecord);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getRssi()
	{
		return rssi;
	}

	public void setRssi(int rssi)
	{
		this.rssi = rssi;
	}

	public byte[] getScanRecord()
	{
		return scanRecord;
	}

	public void setScanRecord(byte[] scanRecord)
	{
		this.scanRecord = scanRecord;
	}

	@Override
	public String toString()
	{
		return "BluetoothDevice [name=" + name + ", address=" + address + ", rssi=" + rssi + "]";
	}
	
	
}
