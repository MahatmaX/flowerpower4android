package de.fp4a.timeseries;

public interface ITimeSeriesModel
{

	public int size();

	public long getTimestamp(int index);

	public float getValue(int index);

	public float getLowestValue();
	
	public float getHighestValue();

}
