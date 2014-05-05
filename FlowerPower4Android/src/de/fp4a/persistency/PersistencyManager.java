package de.fp4a.persistency;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
import de.fp4a.model.FlowerPower;
import de.fp4a.timeseries.TimeSeriesModel;
import de.fp4a.timeseries.dao.ITimeSeriesModelDAO;
import de.fp4a.util.FlowerPowerConstants;

public class PersistencyManager
{
	/** These Ids are used e.g. as filename identifier */
	public final static String TIMESERIES_ID_TEMPERATURE	= "temperature";
	public final static String TIMESERIES_ID_BATTERY		= "battery";
	public final static String TIMESERIES_ID_SUNLIGHT		= "sunlight";
	public final static String TIMESERIES_ID_SOILMOISTURE	= "soilmoisture";
	
	private Timer timer;
	
//	private LinkedList<Long> batteryLevelTimestamps;
//	private LinkedList<Float> batteryLevelValues;
//	
//	private LinkedList<Long> temperatureTimestamps;
//	private LinkedList<Float> temperatureValues;
//	
//	private LinkedList<Long> sunlightTimestamps;
//	private LinkedList<Float> sunlightValues;
//	
//	private LinkedList<Long> soilMoistureTimestamps;
//	private LinkedList<Float> soilMoistureValues;
	
	private TimeSeriesModel batteryLevel;
	private TimeSeriesModel sunlight;
	private TimeSeriesModel temperature;
	private TimeSeriesModel soilMoisture;
	
	public PersistencyManager(Context context)
	{
//		batteryLevelTimestamps 	= new LinkedList<Long>();
//		batteryLevelValues 		= new LinkedList<Float>();
//		temperatureTimestamps 	= new LinkedList<Long>();
//		temperatureValues 		= new LinkedList<Float>();
//		sunlightTimestamps 		= new LinkedList<Long>();
//		sunlightValues 			= new LinkedList<Float>();
//		soilMoistureTimestamps 	= new LinkedList<Long>();
//		soilMoistureValues 		= new LinkedList<Float>();
		 
		batteryLevel 	= new TimeSeriesModel(1000, ITimeSeriesModelDAO.INTERNAL, TIMESERIES_ID_BATTERY, context);
		sunlight 		= new TimeSeriesModel(1000, ITimeSeriesModelDAO.INTERNAL, TIMESERIES_ID_SUNLIGHT, context);
		temperature 	= new TimeSeriesModel(1000, ITimeSeriesModelDAO.INTERNAL, TIMESERIES_ID_TEMPERATURE, context);
		soilMoisture 	= new TimeSeriesModel(1000, ITimeSeriesModelDAO.INTERNAL, TIMESERIES_ID_SOILMOISTURE, context);
		
	}
	
	public void enablePersistency(boolean enable, long period)
	{
		if (enable)
		{
			timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run()
				{
					try
					{
						persist();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			};
			timer.schedule(timerTask, period, period);
		}
		else
		{
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}
	
	public boolean isEnabled()
	{
		return timer != null;
	}
	
	public synchronized void add(FlowerPower fp) throws Exception
	{
		if (fp.getBatteryLevelTimestamp() > 0) // add if set
		{
			batteryLevel.addMeasurement(fp.getBatteryLevelTimestamp(), (float)fp.getBatteryLevel());
//			if (batteryLevelTimestamps.size() == 0) // add if list is empty
//			{
//				batteryLevelTimestamps.add(fp.getBatteryLevelTimestamp());
//				batteryLevelValues.add((float)fp.getBatteryLevel());
//			}
//			else if (fp.getBatteryLevelTimestamp() > batteryLevelTimestamps.getLast()) // add only if newer
//			{
//				batteryLevelTimestamps.add(fp.getBatteryLevelTimestamp());
//				batteryLevelValues.add((float)fp.getBatteryLevel());
//			}
		}
		
		if (fp.getTemperatureTimestamp() > 0) // add if set
		{
			temperature.addMeasurement(fp.getTemperatureTimestamp(), (float)fp.getTemperature());
			
//			if (temperatureTimestamps.size() == 0) // add if list is empty
//			{
//				temperatureTimestamps.add(fp.getTemperatureTimestamp());
//				temperatureValues.add((float)fp.getTemperature());
//			}
//			else if (fp.getTemperatureTimestamp() > temperatureTimestamps.getLast()) // add only if newer
//			{
//				temperatureTimestamps.add(fp.getTemperatureTimestamp());
//				temperatureValues.add((float)fp.getTemperature());
//			}
		}
		
		if (fp.getSunlightTimestamp() > 0) // add if set
		{
			sunlight.addMeasurement(fp.getSunlightTimestamp(), (float)fp.getSunlight());
			
//			if (sunlightTimestamps.size() == 0) // add if list is empty
//			{
//				sunlightTimestamps.add(fp.getSunlightTimestamp());
//				sunlightValues.add((float)fp.getSunlight());
//			}
//			else if (fp.getSunlightTimestamp() > sunlightTimestamps.getLast()) // add only if newer
//			{
//				sunlightTimestamps.add(fp.getSunlightTimestamp());
//				sunlightValues.add((float)fp.getSunlight());
//			}
		} 
		
		if (fp.getSoilMoistureTimestamp() > 0) // add if set
		{
			soilMoisture.addMeasurement(fp.getSoilMoistureTimestamp(), (float)fp.getSoilMoisture());
			
//			if (soilMoistureTimestamps.size() == 0) // add if list is empty
//			{
//				soilMoistureTimestamps.add(fp.getSoilMoistureTimestamp());
//				soilMoistureValues.add((float)fp.getSoilMoisture());
//			}
//			else if (fp.getSoilMoistureTimestamp() > soilMoistureTimestamps.getLast()) // add only if newer
//			{
//				soilMoistureTimestamps.add(fp.getSoilMoistureTimestamp());
//				soilMoistureValues.add((float)fp.getSoilMoisture());
//			}
		}
	}
	
	private synchronized void persist() throws Exception
	{
		if (temperature.size() > 0)
		{
			temperature.save(temperature.getTimestamps(), temperature.getValues(), true);
			temperature.empty();
		}
		
		if (batteryLevel.size() > 0)
		{
			batteryLevel.save(batteryLevel.getTimestamps(), batteryLevel.getValues(), true);
			batteryLevel.empty();
		}
		
		if (sunlight.size() > 0)
		{
			sunlight.save(sunlight.getTimestamps(), sunlight.getValues(), true);
			sunlight.empty();
		}
		
		if (soilMoisture.size() > 0)
		{
			soilMoisture.save(soilMoisture.getTimestamps(), soilMoisture.getValues(), true);
			soilMoisture.empty();
		}
	}
}
