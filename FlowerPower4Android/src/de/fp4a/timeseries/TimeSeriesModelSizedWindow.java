package de.fp4a.timeseries;


public class TimeSeriesModelSizedWindow implements ITimeSeriesModel
{
	private int maxSize = 1000;
	private TimeSeriesModel measurements;
	
	public TimeSeriesModelSizedWindow(TimeSeriesModel measurements, int maxSize)
	{
		this.measurements = measurements;
		setMaxSize(maxSize);
	}
	
	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public int size()
	{
		return measurements.size() > getMaxSize() ? getMaxSize() : measurements.size();
	}

	public long getTimestamp(int index)
	{
		return measurements.size() > getMaxSize() ? measurements.getTimestamp(measurements.size() - getMaxSize() + index) : measurements.getTimestamp(index);
	}

	public float getValue(int index)
	{
		return measurements.size() > getMaxSize() ? measurements.getValue(measurements.size() - getMaxSize() + index) : measurements.getValue(index);
	}

	public float getLowestValue()
	{
		float value = Float.MAX_VALUE;
		for (int i=0; i < size(); i++)
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
		for (int i=0; i < size(); i++)
		{
			float v = getValue(i);
			if (v > value)
				value = v;
		}
		return value;
	}
}
