package de.fp4a.util;

public class Util
{

	public static String data2hex(byte[] data)
	{
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for (int i=data.length-1; i >= 0; i--)
		{
			byte byteChar = data[i];
			stringBuilder.append(String.format("%02X ", byteChar));
		}
		return stringBuilder.toString();
	}

}
