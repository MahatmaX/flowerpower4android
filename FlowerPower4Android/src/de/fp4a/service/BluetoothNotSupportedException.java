package de.fp4a.service;

public class BluetoothNotSupportedException extends RuntimeException
{

	public BluetoothNotSupportedException()
	{
		super();
	}
	
	public BluetoothNotSupportedException(String msg)
	{
		super(msg);
	}
}
