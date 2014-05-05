package de.fp4a.timeseries;

import java.util.Calendar;




public class TimeSeriesModelTimedWindow implements ITimeSeriesModel
{
	public final static String START_AT_NOW = "now";
	public final static String START_AT_HOUR = "hour";
	public final static String START_AT_DAY = "day";
	
	private int timeInMillis = 1000;
	private TimeSeriesModel measurements;
	
	/** These variables are used to optimize getMappedIndex for speed **/
	private int lastMappedIndex = -1;
	private int lastMeasurementSize = -1;
	
	private String startAtBeginning = START_AT_NOW;
	
	/**
	 * @param startAtBeginning Start the interval at the beginning of a new day (DAY), new hour (HOUR) or with no special time ahead. 
	 */
	public TimeSeriesModelTimedWindow(TimeSeriesModel measurements, int timeInMillis, String startAtBeginning)
	{
		this.measurements = measurements;
		this.timeInMillis = timeInMillis;
		this.startAtBeginning = startAtBeginning;
	}
	
	public int getTimeInMillis()
	{
		return timeInMillis;
	}

	public void setTimeInMillis(int timeInMillis)
	{
		this.timeInMillis = timeInMillis;
	}

	public int size()
	{
		int size = measurements.size() - getMappedIndex();
		return size;
	}

	public long getTimestamp(int index)
	{
		return measurements.getTimestamp(getMappedIndex() + index);
	}

	public float getValue(int index)
	{
		return measurements.getValue(getMappedIndex() + index);
	}

	/**
	 * Get the index of the original data set at which all subsequent entries have a higher timestamp and shall be included in the view
	 * @return
	 */
	private synchronized int getMappedIndex() 
	{
		if ((lastMappedIndex >= 0) && (measurements.size() == lastMeasurementSize))
			return lastMappedIndex;
		
		int returnVal = 0;
		long windowInterval = System.currentTimeMillis() - timeInMillis;
		Calendar c = null;
		
		for (int i=measurements.size()-1; i >= 0; i--)
		{
//			LogHandler.modelLevel(this, "getMappedIndex: i=" + i + " " + TimeAndDateHelper.getHumanReadableTimestamp(measurements.getTimestamp(i), true) + " " + measurements.getValue(i));
			// as soon as a lower timestamp is discovered return the size
			// e.g. if 1000 measurements are present, i counts down from 1000 to 0
			// if at index 300 a lower timestamp is discovered return 1000 - 300 = 700

			if (measurements.getTimestamp(i) < windowInterval)
			{
				/*
				 * depending on startAtTheBeginning, three different modes are possible:
				 * START_AT_NOW simply uses the value of the time window and includes all values from now until (now - window value).
				 * E.g. in a time window of 5 days all measurements from the last 5 * 24 hours are included.
				 * START_AT_DAY increases the window size to include full days.
				 * E.g. now is 7 o'clock pm, instead of just using the last 5*24 hours the window is increased to include the last 5*24 + 19 hours, starting at midnight.
				 */
			
				if (startAtBeginning.equals(START_AT_NOW))
				{
					returnVal = i;
					break;
				}
				else if (startAtBeginning.equals(START_AT_DAY) || startAtBeginning.equals(START_AT_HOUR))
				{
					if (c == null)
					{
						// initialize calendar so that it points to last midnight 
						c = Calendar.getInstance();
				        c.setTimeInMillis(measurements.getTimestamp(i));
				        if (startAtBeginning.equals(START_AT_DAY))
				        	c.set(Calendar.HOUR_OF_DAY, 0);
				        c.set(Calendar.MINUTE, 0);
				        c.set(Calendar.SECOND, 0);
				        c.set(Calendar.MILLISECOND, 0);
					}
					else if (measurements.getTimestamp(i) < c.getTimeInMillis())
					{
						returnVal = i;
						break;
					}
				}
			}
		}
		lastMappedIndex = returnVal;
		lastMeasurementSize = measurements.size();
		return returnVal;
	}
	
	public float getLowestValue()
	{
		float value = Float.MAX_VALUE;
		int size = size();
		for (int i=0; i < size; i++)
		{
			float v = getValue(i);
			if (v < value)
				value = v;
		}
		return value;
	}

	public float getHighestValue()
	{
		float value = 0;
		int size = size();
		for (int i=0; i < size; i++)
		{
			float v = getValue(i);
			if (v > value)
				value = v;
		}
		return value;
	}
}
