package de.fp4a.timeseries;

import java.io.Serializable;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import de.fp4a.timeseries.dao.ITimeSeriesModelDAO;
import de.fp4a.timeseries.dao.TimeSeriesModelExternalStorageDAO;
import de.fp4a.timeseries.dao.TimeSeriesModelInternalStorageDAO;
import de.fp4a.timeseries.dao.TimeSeriesModelSQLiteDAO;
import de.fp4a.util.FlowerPowerConstants;
import de.fp4a.util.Util;

public class TimeSeriesModel implements Serializable, ITimeSeriesModel
{
	private static final long serialVersionUID = 8754333652958394003L;
	
	private LinkedList<Long> timestamps;
	private LinkedList<Float> values;
	
	private static float lowestValue = -1;
	private static float highestValue = 0;
	
	private static int maxListSize;
	private double simplificationTolerance = 0.1;
	
	private transient ITimeSeriesModelDAO modelDAO;
	
	/**
	 * 
	 * @param maxListSize
	 * @param location  One of FlowerPowerConstants.PERSISTENCY_XYZ
	 * @param fileName  The name of the file/table in which data shall be stored
	 * @param context  The context of the calling Activity, Service, Application etc.
	 */
	public TimeSeriesModel(int maxListSize, String storageLocation, String fileName, Context context)
	{
		timestamps = new LinkedList<Long>();
        values = new LinkedList<Float>();
        TimeSeriesModel.maxListSize = maxListSize;
        
		setStorageLocation(storageLocation, fileName, context);
	}

	public synchronized LinkedList<Long> getTimestamps()
	{
		return timestamps;	
	}

	public synchronized long getTimestamp(int index)
	{
		return timestamps.get(index);
	}
	
	public LinkedList<Float> getValues()
	{
		return values;
	}

	public synchronized float getValue(int index)
	{
		return values.get(index);
	}
	
	/**
	 * @throws Exception  Only if saving after compaction fails.
	 */
	public synchronized void addMeasurement(long timestamp, float value) throws Exception
	{
		// synchronize ! Let's imagine we get a new sensor value every second, but compaction requires 2 seconds, 
		// we will end up compacting endlessly, because every new arriving sensor value calls compaction again !
		
		if (timestamps.size() == 0) // add if list is empty
		{
			timestamps.addLast(timestamp);
			values.addLast(value);
		}
		else if (timestamp > timestamps.getLast()) // add only if newer
		{
			timestamps.addLast(timestamp);
			values.addLast(value);
		}
		else
			return; // add nothing
		
		if (value < lowestValue)
			lowestValue = value;
		else if (value > highestValue)
			highestValue = value;
		
		if (size() > maxListSize)
		{
			compactLists(); // minMax is recalculated here as well
			save(timestamps, values, false);
		}
	}
	
	private synchronized static void recalculateLowestAndHighestValue(LinkedList<Float> values)
	{
		lowestValue = 1999;
		highestValue = -1999;
		
		for (int i=0; i < values.size(); i++)
		{
			if (values.get(i) < lowestValue)
				lowestValue = values.get(i);
			if (values.get(i) > highestValue)
				highestValue = values.get(i);
		}
	}
	
	public synchronized float getLowestValue()
	{
		return lowestValue;
	}
	
	public synchronized float getHighestValue()
	{
		return highestValue;
	}
	
	public int getMaxListSize()
	{
		return maxListSize;
	}
	
	public synchronized void setMaxListSize(int maxListSize) throws Exception
	{
		TimeSeriesModel.maxListSize = maxListSize;
		
		boolean savingRequired = false;
		while (size() > maxListSize)
		{
			compactLists();
			savingRequired = true;
		}
		if (savingRequired)
			save(timestamps, values, false);
	}
	
	public void setSimplificationTolerance(double tolerance)
	{
		this.simplificationTolerance = tolerance;
	}

	/**
	 * Compact lists by building an average of every two elements if the history size exceeds maxListSize.
	 * For large datasets this means, that old entries are more often compacted than new entries.
	 * As a result, old entries are more or less vague, while new entries are still quite accurate.
	 * @param timestamps
	 * @param values
	 */
	private synchronized void compactLists()
	{
		long startTime = System.currentTimeMillis();
		Log.i(FlowerPowerConstants.TAG, "going to compact measurement history, current size=" + timestamps.size() + " (max size=" + maxListSize + ")");
		
		try
		{
			douglasPeucker();
		}
		catch(Error e)
		{
			// this is probably a StackOverflowError, seems to happen on very large lists (> 10,000)
			// Do not care about it, just proceed because the list is compacted below.
		}
		
		if (size() >= (maxListSize - (maxListSize/100)))
		{
			// if douglas peucker could not further compact the list, build averages
			for (int i=0; i < timestamps.size() - 1; i++) // we need to only add +1 to i, cause we are removing two elements and adding one new element from the list within the loop.
			{
				long timestampA = timestamps.remove(i);
				float valueA = values.remove(i);
				long timestampB = timestamps.remove(i);
				float valueB = values.remove(i);
				
				long timestampNew = (long)((timestampA + timestampB) / 2);
				float valueNew = (float)((valueA + valueB) / 2f);
				 
				timestamps.add(i, timestampNew);
				values.add(i, valueNew);
			}
		}
		
		Log.i(FlowerPowerConstants.TAG, "compacting finished: new size=" + timestamps.size() + " (max size=" + maxListSize + "), it took " + (System.currentTimeMillis() - startTime) + " ms");
		recalculateLowestAndHighestValue(values);
	}
	
	/**
	 * Line simplification
	 */
	private void douglasPeucker()
	{
		long startTimeMillis = System.currentTimeMillis();
		Log.i(FlowerPowerConstants.TAG, "Start Douglas Peucker with " + size() + " entries");
		
		// LinkedList -> array
		Long[] x = new Long[timestamps.size()];
		Float[] y = new Float[values.size()];
		timestamps.toArray(x);
		values.toArray(y);
		
		// simplify
		boolean[] keep = TimeSeriesLineSimplification.simplify(x, y, simplificationTolerance);

		// sort out the entries not to keep
		timestamps = new LinkedList<Long>();
		values = new LinkedList<Float>();
		for (int i=0; i < x.length; i++)
		{
			if (keep[i])
			{
				timestamps.add(x[i]);
				values.add(y[i].floatValue());
			}
		}
		
		Log.i(FlowerPowerConstants.TAG, "Douglas Peucker took " + (System.currentTimeMillis() - startTimeMillis) + " ms, left " + size() + " entries");
	}
	
	public synchronized int size()
	{
		return timestamps.size();
	}
	
	public synchronized int size(long fromTimeInMillis, long toTimeInMillis)
	{
		int startIndex = 0;
		int endIndex = timestamps.size()-1;
		
		if (timestamps.size() == 0)
			return 0;
		
		while(timestamps.get(startIndex) < fromTimeInMillis)
		{
			// if a future start index is chosen, simply return 0 (cause no future measurements have been taken yet)
			if (++startIndex == timestamps.size())
				return 0;
		}
		
		while(timestamps.get(endIndex) > toTimeInMillis)
		{
			if (--endIndex < 0)
				return timestamps.size();
		}
		
		return (endIndex - startIndex) + 1;
	}
	
	public synchronized void load() throws Exception
	{
		timestamps = new LinkedList<Long>();
		values = new LinkedList<Float>();
		
		modelDAO.load(timestamps, values);
		
		if (size() > maxListSize)
		{
			compactLists(); // minMax is recalculated here as well
			save(timestamps, values, false);
		}
		
		recalculateLowestAndHighestValue(values);
	}
	
	public void save(LinkedList<Long> timestamps, LinkedList<Float> values, boolean append) throws Exception
	{
		modelDAO.save(timestamps, values, append);
	}
	
	public synchronized void clearAll()
	{
		Log.i(FlowerPowerConstants.TAG, "Going to clear measurements ...");
		
		modelDAO.deleteDataFiles();
		
		timestamps.clear();
		values.clear();
	}
	
	public synchronized void clear(long fromTimeInMillis, long toTimeInMillis) throws Exception
	{
		Log.i(FlowerPowerConstants.TAG, "Going to clear measurements from " + Util.getHumanReadableTimestamp(fromTimeInMillis, true) + " to " + Util.getHumanReadableTimestamp(toTimeInMillis, true) + " ...");
		
		if (timestamps.size() == 0) // nothing to clear
			return;
		
		int startIndex = 0;
		while(timestamps.get(startIndex) < fromTimeInMillis)
		{
			// if a future start index is chosen, simply return 0 (cause no future measurements have been taken yet)
			if (++startIndex == timestamps.size())
				return;
		}
		
		while ((startIndex < timestamps.size()) && (timestamps.get(startIndex) <= toTimeInMillis))
		{
			timestamps.remove(startIndex);
			values.remove(startIndex);
		}
		
		recalculateLowestAndHighestValue(values);
		save(timestamps, values, false);
	}
	
	public synchronized void empty()
	{
		timestamps.clear();
		values.clear();
		
		lowestValue = -1999;
		highestValue = 1999;
	}
	
	/**
	 * Set the location where timeseries shall be stored.
	 * @param storageLocation  One of IMeasurementModelDAO.EXTERNAL, IMeasurementModelDAO.INTERNAL, IMeasurementModelDAO.SQLITE
	 * @param fileName  The name of the file/table in which data shall be stored
	 * @param context  The context of the calling Activity, Service, Application etc.
	 */
	public synchronized void setStorageLocation(String storageLocation, String fileName, Context context)
	{
		// delete old data
		if (modelDAO != null) // upon startup it's null
		{
			Log.i(FlowerPowerConstants.TAG, "Change storage location from " + modelDAO.getClass().getSimpleName() + " to " + storageLocation);
			modelDAO.deleteDataFiles(); // remove data from 'old' DAO
		}
		
		// create new DAO
		if (storageLocation.equals(FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_EXTERNAL))
			modelDAO = new TimeSeriesModelExternalStorageDAO(fileName, context);
		else if (storageLocation.equals(FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL))
			modelDAO = new TimeSeriesModelInternalStorageDAO(fileName, context);
		else
			modelDAO = new TimeSeriesModelSQLiteDAO(fileName, context);
		
		if (timestamps.size() > 0) // timestamps.size() == 0 upon startup. Do not overwrite an existing history with an empty list !
		{
			new Thread(new Runnable() {
				public void run()
				{
					try
					{
						modelDAO.save(timestamps, values, false);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					} 
				} 
			}).start();
		}
	}
}
