package de.fp4a.gui;

import com.androidplot.xy.BoundaryMode;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.fp4a.R;
import de.fp4a.gui.plot.XYTimeSeriesPlot;
import de.fp4a.model.FlowerPower;
import de.fp4a.persistency.PersistencyManager;
import de.fp4a.service.FlowerPowerServiceManager;
import de.fp4a.service.IFlowerPowerDevice;
import de.fp4a.service.IFlowerPowerServiceListener;
import de.fp4a.service.IFlowerPowerServiceManager;
import de.fp4a.timeseries.TimeSeriesModel;
import de.fp4a.util.FlowerPowerConstants;

public class FlowerPowerPlotFragment extends Fragment
{
	private IFlowerPowerServiceManager serviceManager;
	
	private XYTimeSeriesPlot plot;
	private TimeSeriesModel timeSeries;
	
	private String seriesType;
	private String seriesId;
	private int maxHistorySize = -1;
	private String storageLocation;
	
	/**
	 * Once the Activity to which this fragment belongs is created, this listener is registered with the 
	 * serviceManager to receive updates subsequently
	 */
	private IFlowerPowerServiceListener serviceListener = new IFlowerPowerServiceListener() {
		
		public void serviceFailed() { }
		public void serviceDisconnected() { }
		public void serviceConnected() { }
		public void deviceReady(IFlowerPowerDevice device) { }
		public void deviceDisconnected() { }
		public void deviceConnected() { }
		public void dataAvailable(FlowerPower fp)
		{
			float oldLowest = timeSeries.getLowestValue();
			float oldHighest = timeSeries.getHighestValue();
			
			// depending on the kind of time series, add the corresponding timestamp and value and simply redraw the plot
			try
			{ 
				if (seriesType.equals(PersistencyManager.TIMESERIES_TYPE_TEMPERATURE))
					timeSeries.addMeasurement(fp.getTemperatureTimestamp(), (float)fp.getTemperature());
				else if (seriesType.equals(PersistencyManager.TIMESERIES_TYPE_SUNLIGHT))
					timeSeries.addMeasurement(fp.getSunlightTimestamp(), (float)fp.getSunlight());
				else if (seriesType.equals(PersistencyManager.TIMESERIES_TYPE_SOILMOISTURE))
					timeSeries.addMeasurement(fp.getSoilMoistureTimestamp(), (float)fp.getSoilMoisture());
				else if (seriesType.equals(PersistencyManager.TIMESERIES_TYPE_BATTERY))
					timeSeries.addMeasurement(fp.getBatteryLevelTimestamp(), (float)fp.getBatteryLevelTimestamp());
				else
					Log.w(FlowerPowerConstants.TAG, "FlowerPowerPlotFragment: Unknown series type: " + seriesType + ". Cannot update view.");
				
				Log.d(FlowerPowerConstants.TAG, "SeriesType=" + seriesType + " Timeseries low=" + timeSeries.getLowestValue() + " high=" + timeSeries.getHighestValue());
				
				if ( (timeSeries.getLowestValue() != oldLowest) || (timeSeries.getHighestValue() != oldHighest))
					plot.setRangeBoundaries(timeSeries.getLowestValue() - (timeSeries.getLowestValue() / 10), 
							timeSeries.getHighestValue() + (timeSeries.getHighestValue() / 10), BoundaryMode.FIXED);
				
				plot.redraw();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.flowerpower_plot_fragment, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		FlowerPowerPlotActivity act = (FlowerPowerPlotActivity)getActivity();
		
		Intent intent = act.getIntent();
		final String deviceAddress = intent.getStringExtra(FlowerPowerConstants.EXTRAS_DEVICE_ADDRESS);
		
		serviceManager = FlowerPowerServiceManager.getInstance(deviceAddress, act);
		serviceManager.addServiceListener(serviceListener);
	}
	
	public void onDestroy()
	{
		serviceManager.removeServiceListener(serviceListener);
		super.onDestroy();
	}
	
	/**
	 * Initialize this fragment with time series data and visualization options.
	 * 
	 * @param seriesType  The type of the time series as specified by the PersistencyManager (e.g. PersistencyManager.TIMESERIES_TYPE_TEMPERATURE)
	 * @param seriesId  The individual Id of the time series to display
	 * @param maxHistorySize  The max. history size (required for loading or creating a time series)
	 * @param storageLocation  The location where the history is stored (required for loading a time series)
	 * @param plotTitle  The title that shall be shown above the plot
	 * @param gradientColorStart  The start color of the gradient to fill the plot
	 * @param gradientColorEnd  The end color of the gradient to fill the plot
	 * @param gradientEndCoordinateY  The pixel y-index where the color gradient shall end (and be mirrored)
	 * @param lineAndPointColor  The color of the line and all points
	 */
	public void init(String seriesType, String seriesId, int maxHistorySize, String storageLocation, String plotTitle, 
			int gradientColorStart, int gradientColorEnd, int gradientEndCoordinateY,
			int lineAndPointColor)
	{
		this.seriesType = seriesType;
		this.seriesId = seriesId;
		this.maxHistorySize = maxHistorySize;
		this.storageLocation = storageLocation;
		
		PersistencyManager pm = PersistencyManager.getInstance(getActivity());
		try
		{
			timeSeries = pm.load(maxHistorySize, storageLocation, seriesType, seriesId);
		}
		catch (Exception e)
		{
			timeSeries = pm.create(maxHistorySize, storageLocation, seriesType, seriesId);
			e.printStackTrace();
		}
		
		TextView title = (TextView)getView().findViewById(R.id.plot_title);
		title.setText(plotTitle);
		
		plot = (XYTimeSeriesPlot) getView().findViewById(R.id.plot);

		// init plot with auto-scale boundaries (10% upper and lower padding)
		plot.init(timeSeries, plotTitle, (int)(timeSeries.getLowestValue() - (timeSeries.getLowestValue() / 10)), 
				(int)(timeSeries.getHighestValue() + (timeSeries.getHighestValue() / 10)),
				gradientColorStart, gradientColorEnd, gradientEndCoordinateY,
				lineAndPointColor);
	}
	
	/**
	 * Clear this fragment and delete the history of the persisted time series that is shown in this plot.
	 */
	public void clear()
	{
		PersistencyManager pm = PersistencyManager.getInstance(getActivity());
		pm.clear(seriesType, seriesId, storageLocation); // clear persistent storage
		
		timeSeries.clearAll(); // clear in-memory copy
		
		plot.redraw();
	}
}
