package de.fp4a.timeseries.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import de.fp4a.util.FlowerPowerConstants;
import de.fp4a.util.Util;

public class TimeSeriesModelExternalStorageDAO implements ITimeSeriesModelDAO
{
	private Context context;
	private String fileName;
	
	public TimeSeriesModelExternalStorageDAO(String fileName, Context context)
	{
		this.context = context;
		this.fileName = fileName;
	}
	
	public synchronized void load(LinkedList<Long> timestamps, LinkedList<Float> values) throws Exception
	{
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		{
		    // We can read and write the media
		    mExternalStorageAvailable = true;
		} 
		
		if (!mExternalStorageAvailable)
			throw new RuntimeException("Unable to read from SD card.");
		
		File file = new File(context.getExternalFilesDir(null), fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		// clear existing lists
		timestamps.clear();
		values.clear();
		
		String inputLine;
		while ((inputLine = br.readLine()) != null) 
		{
			StringTokenizer tokenizer = new StringTokenizer(inputLine, " ");
//				String humanReadableTimestamp = tokenizer.nextToken(); // not used
			try
			{
				tokenizer.nextToken();
				float value = Float.parseFloat(tokenizer.nextToken());
				long timestamp = Long.parseLong(tokenizer.nextToken());

				timestamps.addLast(timestamp);
				values.addLast(value);
			}
			catch(Exception exc)
			{
				// This may happen for example, if - while storing a measurement - the device stops the service in order to reboot
				Log.e(FlowerPowerConstants.TAG, "MeasurementModelExternalStorageDAO.load: Failed to parse input line: " + inputLine);
				exc.printStackTrace();
			}
		}
		br.close();
		Log.i(FlowerPowerConstants.TAG, "Loaded " + timestamps.size() + " measurements ...");
	}

	public synchronized void save(LinkedList<Long> timestamps, LinkedList<Float> values, boolean append) throws Exception
	{
		Log.i(FlowerPowerConstants.TAG, "Going to save " + timestamps.size() + " measurements ...");
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{
		    // We can read and write the media
		    mExternalStorageWriteable = true;
		} 
		
		if (!mExternalStorageWriteable)
			throw new RuntimeException("Unable to write to SD card.");
		
		File file = new File(context.getExternalFilesDir(null), fileName);
		FileOutputStream fos = new FileOutputStream(file, append);
		
		for (int i=0; i < timestamps.size(); i++)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Util.getHumanReadableTimestamp(timestamps.get(i), false));
			sb.append(" ");
			sb.append(values.get(i));
			sb.append(" ");
			sb.append(timestamps.get(i));
			sb.append("\n");
			
			fos.write(sb.toString().getBytes());
		}
		fos.close();
	}
	
	public synchronized void deleteDataFiles()
	{
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{
		    // We can read and write the media
		    mExternalStorageWriteable = true;
		} 
		
		if (!mExternalStorageWriteable)
			throw new RuntimeException("Unable to write to SD card.");
		
		File file = new File(context.getExternalFilesDir(null), fileName);
		file.delete();
		
		Log.d(FlowerPowerConstants.TAG, "History file successfully deleted");
	}
}
