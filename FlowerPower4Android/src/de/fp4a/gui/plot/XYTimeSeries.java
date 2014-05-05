package de.fp4a.gui.plot;

import com.androidplot.xy.XYSeries;

import de.fp4a.timeseries.ITimeSeriesModel;

public class XYTimeSeries implements XYSeries
{
	private ITimeSeriesModel series;
	private String title;
	
	public XYTimeSeries(ITimeSeriesModel series, String title)
	{
		this.series = series;
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}

	public Number getX(int i)
	{
		return series.getTimestamp(i);
	}

	public Number getY(int i)
	{
		return series.getValue(i);
	}

	public int size()
	{
		return series.size();
	}

}
