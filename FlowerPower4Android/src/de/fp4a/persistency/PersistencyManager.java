package de.fp4a.persistency;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import de.fp4a.model.FlowerPower;
import de.fp4a.timeseries.TimeSeriesModel;

public class PersistencyManager
{
	/** These Ids are used e.g. as filename identifier */
	public final static String TIMESERIES_TYPE_TEMPERATURE	= "temperature";
	public final static String TIMESERIES_TYPE_BATTERY		= "battery";
	public final static String TIMESERIES_TYPE_SUNLIGHT		= "sunlight";
	public final static String TIMESERIES_TYPE_SOILMOISTURE	= "soilmoisture";
	
	private Timer timer;
	private Map<String, TimerTask> timerTasks;
	private Context context;
	
	private Map<String, TimeSeriesModel> seriesToBePersisted;
	
	private static PersistencyManager instance;
	
	private PersistencyManager(Context context)
	{
		seriesToBePersisted = new HashMap<String, TimeSeriesModel>();
		timer = new Timer();
		timerTasks = new HashMap<String, TimerTask>();
		
		this.context = context;
	}
	
	public static PersistencyManager getInstance(Context context)
	{
		if (instance == null)
			instance = new PersistencyManager(context);
		
		return instance;
	}
	
	public void enablePersistency(long period, int maxListSize, String storageLocation, String seriesId)
	{
		enablePersistency(period, maxListSize, storageLocation, TIMESERIES_TYPE_BATTERY, seriesId);
		enablePersistency(period, maxListSize, storageLocation, TIMESERIES_TYPE_SUNLIGHT, seriesId);
		enablePersistency(period, maxListSize, storageLocation, TIMESERIES_TYPE_TEMPERATURE, seriesId);
		enablePersistency(period, maxListSize, storageLocation, TIMESERIES_TYPE_SOILMOISTURE, seriesId);
	}
	
	public void enablePersistency(long period, int maxListSize, String storageLocation, String seriesType, String seriesId)
	{
		final TimeSeriesModel timeSeries = create(maxListSize, storageLocation, seriesType, seriesId);
		
		TimerTask timerTask = new TimerTask() {
			public void run()
			{
				try
				{
					persist(timeSeries);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		timer.schedule(timerTask, period, period);
		
		seriesToBePersisted.put(seriesType + "_" + seriesId, timeSeries);
		timerTasks.put(seriesType + "_" + seriesId, timerTask);
	}
	
	public void disablePersistency(String seriesId)
	{
//		Iterator<String> iter = seriesToBePersisted.keySet().iterator();
//		while (iter.hasNext())
//		{
//			String key = iter.next();
//			String seriesType = key.substring(0, key.indexOf("_"));
//			String seriesId = key.substring(key.indexOf("_") + 1);
//			disablePersistency(seriesType, seriesId);
//		}
		disablePersistency(TIMESERIES_TYPE_BATTERY, seriesId);
		disablePersistency(TIMESERIES_TYPE_SOILMOISTURE, seriesId);
		disablePersistency(TIMESERIES_TYPE_SUNLIGHT, seriesId);
		disablePersistency(TIMESERIES_TYPE_TEMPERATURE, seriesId);
	}
	
	public void disablePersistency(String seriesType, String seriesId)
	{
		String key = seriesType + "_" + seriesId;
		try
		{
			persist(seriesToBePersisted.get(key));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		seriesToBePersisted.remove(key);
		
		TimerTask tt = timerTasks.remove(key);
		tt.cancel();
		timer.purge();
	}
	
	public boolean isEnabled(String seriesType, String seriesId)
	{
		return seriesToBePersisted.containsKey(seriesType + "_" + seriesId);
	}
	
	public boolean isEnabled(String seriesId)
	{
		return isEnabled(TIMESERIES_TYPE_BATTERY, seriesId) && isEnabled(TIMESERIES_TYPE_SOILMOISTURE, seriesId) &&
			isEnabled(TIMESERIES_TYPE_SUNLIGHT, seriesId) && isEnabled(TIMESERIES_TYPE_TEMPERATURE, seriesId);
	}
	
	public synchronized void addDataSet(FlowerPower fp, String seriesId) throws Exception
	{
		if ((fp.getBatteryLevelTimestamp() > 0) && isEnabled(TIMESERIES_TYPE_BATTERY, seriesId)) // add if set
		{
			if (fp.getBatteryLevel() >= 0)
				seriesToBePersisted.get(TIMESERIES_TYPE_BATTERY + "_" + seriesId).addMeasurement(fp.getBatteryLevelTimestamp(), (float)fp.getBatteryLevel());
		}
		
		if ((fp.getTemperatureTimestamp() > 0) && isEnabled(TIMESERIES_TYPE_TEMPERATURE, seriesId)) // add if set
		{
			if (fp.getTemperature() != -1)
				seriesToBePersisted.get(TIMESERIES_TYPE_TEMPERATURE+ "_" + seriesId).addMeasurement(fp.getTemperatureTimestamp(), (float)fp.getTemperature());
		}
		
		if ((fp.getSunlightTimestamp() > 0) && isEnabled(TIMESERIES_TYPE_SUNLIGHT, seriesId)) // add if set
		{
			if (fp.getSunlight() >= 0)
				seriesToBePersisted.get(TIMESERIES_TYPE_SUNLIGHT+ "_" + seriesId).addMeasurement(fp.getSunlightTimestamp(), (float)fp.getSunlight());
		} 
		
		if ((fp.getSoilMoistureTimestamp() > 0) && isEnabled(TIMESERIES_TYPE_SOILMOISTURE, seriesId)) // add if set
		{
			if (fp.getSoilMoisture() >= 0)
				seriesToBePersisted.get(TIMESERIES_TYPE_SOILMOISTURE + "_" + seriesId).addMeasurement(fp.getSoilMoistureTimestamp(), (float)fp.getSoilMoisture());
		}
	}
	
	public TimeSeriesModel load(int maxHistorySize, String storageLocation, String seriesType, String seriesId) throws Exception
	{
		TimeSeriesModel timeSeries = create(maxHistorySize, storageLocation, seriesType, seriesId);
		timeSeries.load();
		return timeSeries;
	}
	
	public TimeSeriesModel create(int maxHistorySize, String storageLocation, String seriesType, String seriesId)
	{
		return new TimeSeriesModel(maxHistorySize, storageLocation, seriesType + "_" + seriesId, context);
	}
	
	public void clear(String seriesType, String seriesId, String storageLocation)
	{
		create(Integer.MAX_VALUE, storageLocation, seriesType, seriesId).clearAll();
	}
	
	private synchronized void persist(TimeSeriesModel timeSeries) throws Exception
	{
		if (timeSeries.size() > 0)
		{
			timeSeries.save(timeSeries.getTimestamps(), timeSeries.getValues(), true);
			timeSeries.empty();
		}
	}
}
