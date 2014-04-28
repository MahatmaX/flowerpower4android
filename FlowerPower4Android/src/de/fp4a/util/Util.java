package de.fp4a.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static String getHumanReadableTimestamp(long timestamp, boolean includeDate)
	{
		Date date = new Date(timestamp);
		if (includeDate)
			return dateFormat.format(date);
		else
			return timeFormat.format(date);
	}
}
