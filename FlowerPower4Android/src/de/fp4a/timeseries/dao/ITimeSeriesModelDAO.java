package de.fp4a.timeseries.dao;

import java.util.LinkedList;

public interface ITimeSeriesModelDAO
{
	public final static String EXTERNAL = "external";
	public final static String INTERNAL = "internal";
	public final static String SQLITE = "sqlite";
	
	public void load(LinkedList<Long> timestamps, LinkedList<Float> values) throws Exception;
	public void save(LinkedList<Long> timestamps, LinkedList<Float> values, boolean append) throws Exception;
	public void deleteDataFiles();
}
