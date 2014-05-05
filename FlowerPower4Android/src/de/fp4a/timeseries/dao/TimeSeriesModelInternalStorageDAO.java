package de.fp4a.timeseries.dao;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;
import de.fp4a.util.FlowerPowerConstants;
import de.fp4a.util.Util;

public class TimeSeriesModelInternalStorageDAO implements ITimeSeriesModelDAO
{
	private Context context;
	private String fileName;
	
	public TimeSeriesModelInternalStorageDAO(String fileName, Context context)
	{
		this.fileName = fileName;
		this.context = context;
	}
	
	public synchronized void load(LinkedList<Long> timestamps, LinkedList<Float> values) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
		
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
				Log.e(FlowerPowerConstants.TAG, "MeasurementModelInternalStorageDAO.load: Failed to parse input line: " + inputLine);
				exc.printStackTrace();
			}
		}
		br.close();
		Log.i(FlowerPowerConstants.TAG, "Loaded " + timestamps.size() + " measurements ...");
	}

	public synchronized void save(LinkedList<Long> timestamps, LinkedList<Float> values, boolean append) throws Exception
	{
		Log.d(FlowerPowerConstants.TAG, "Going to save " + timestamps.size() + " measurements ...");
		
		FileOutputStream fos = context.openFileOutput(fileName, append ? Context.MODE_APPEND : Context.MODE_PRIVATE);
		
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
		context.deleteFile(fileName);
	}

}
