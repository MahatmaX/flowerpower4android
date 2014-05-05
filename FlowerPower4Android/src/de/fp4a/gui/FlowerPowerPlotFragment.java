package de.fp4a.gui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.fp4a.R;
import de.fp4a.gui.plot.XYTimeSeriesPlot;
import de.fp4a.model.FlowerPower;
import de.fp4a.persistency.PersistencyManager;
import de.fp4a.service.FlowerPowerServiceManager;
import de.fp4a.service.IFlowerPowerDevice;
import de.fp4a.service.IFlowerPowerServiceListener;
import de.fp4a.service.IFlowerPowerServiceManager;
import de.fp4a.timeseries.TimeSeriesModel;
import de.fp4a.timeseries.dao.ITimeSeriesModelDAO;
import de.fp4a.util.FlowerPowerConstants;

public class FlowerPowerPlotFragment extends Fragment
{
	private IFlowerPowerServiceManager serviceManager;
	
	private XYTimeSeriesPlot plot;
	private TimeSeriesModel timeSeries;
	private String timeSeriesId;
	
	private IFlowerPowerServiceListener serviceListener = new IFlowerPowerServiceListener() {
		
		public void serviceFailed() { }
		public void serviceDisconnected() { }
		public void serviceConnected() { }
		public void deviceReady(IFlowerPowerDevice device) { }
		public void deviceDisconnected() { }
		public void deviceConnected() { }
		public void dataAvailable(FlowerPower fp)
		{
			try
			{
				if (timeSeriesId == PersistencyManager.TIMESERIES_ID_TEMPERATURE)
					timeSeries.addMeasurement(fp.getTemperatureTimestamp(), (float)fp.getTemperature());
				else if (timeSeriesId == PersistencyManager.TIMESERIES_ID_SUNLIGHT)
					timeSeries.addMeasurement(fp.getSunlightTimestamp(), (float)fp.getSunlight());
				else if (timeSeriesId == PersistencyManager.TIMESERIES_ID_SOILMOISTURE)
					timeSeries.addMeasurement(fp.getSoilMoistureTimestamp(), (float)fp.getSoilMoisture());
				
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
	}
	
	/**
	 * 
	 * @param timeSeriesId  The id of the time series as specified by the PersistencyManager (e.g. PersistencyManager.TIMESERIES_ID_TEMPERATURE)
	 * @param plotName  The name that shall be shown on the plot
	 * @param plotLowerRangeBounday  The lower boundary of possible values
	 * @param plotUpperRangeBoundary  The upper boundary of possible values
	 */
	public void init(String timeSeriesId, String plotName, 
			int plotLowerRangeBounday, int plotUpperRangeBoundary)
	{
		this.timeSeriesId = timeSeriesId;
		
		try
		{
			timeSeries = new TimeSeriesModel(1000, ITimeSeriesModelDAO.INTERNAL, timeSeriesId, this.getActivity());
//			timeSeries.load();
		}
		catch (Exception e)
		{ 
			e.printStackTrace();
		}
		
		plot = (XYTimeSeriesPlot) getView().findViewById(R.id.plot);
		plot.init(timeSeries, plotName, plotLowerRangeBounday, plotUpperRangeBoundary);
	}
}
