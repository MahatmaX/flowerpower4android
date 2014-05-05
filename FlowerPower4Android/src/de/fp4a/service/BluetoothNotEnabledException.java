package de.fp4a.service;

public class BluetoothNotEnabledException extends RuntimeException
{

	public BluetoothNotEnabledException()
	{
		super();
	}
	
	public BluetoothNotEnabledException(String msg)
	{
		super(msg);
	}
	
}
