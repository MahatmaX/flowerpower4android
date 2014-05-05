package de.fp4a.model;

import java.io.Serializable;

public class FlowerPower implements Serializable
{
	/** ToDo: implement seriesId ? */
	
	private static final long serialVersionUID = 5156641917291655184L;

	private int batteryLevel 	= -1;
	private long batteryLevelTimestamp = -1;
	
	private double temperature	= -1;
	private long temperatureTimestamp = -1;
	
	private double soilMoisture	= -1;
	private long soilMoistureTimestamp = -1;
	
	private double sunlight 	= -1;
	private long sunlightTimestamp = -1;
	
	private FlowerPowerMetadata metadata;
	
	public FlowerPower()
	{
		metadata = new FlowerPowerMetadata();
	}

	public int getBatteryLevel()
	{
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel)
	{
		this.batteryLevel = batteryLevel;
		this.batteryLevelTimestamp = System.currentTimeMillis();
	}

	public long getBatteryLevelTimestamp()
	{
		return batteryLevelTimestamp;
	}
	
	public double getTemperature()
	{
		return temperature;
	}

	public void setTemperature(double temperature)
	{
		this.temperature = temperature;
		this.temperatureTimestamp = System.currentTimeMillis();
	}
	
	public long getTemperatureTimestamp()
	{
		return temperatureTimestamp;
	}
	
	public double getSoilMoisture()
	{
		return soilMoisture;
	}
	
	public void setSoilMoisture(double soilMoisture)
	{
		this.soilMoisture = soilMoisture;
		this.soilMoistureTimestamp = System.currentTimeMillis();
	}

	public long getSoilMoistureTimestamp()
	{
		return soilMoistureTimestamp;
	}
	
	public double getSunlight()
	{
		// sunlight is PPF (photons per square meter), convert to lux
		// according to http://www.apogeeinstruments.com/conversion-ppf-to-lux/
//		if (sunlight != -1)
//			return sunlight * 54; // -1 indicates no value has been set. Do not return -54, cause this could confuse users 
		return sunlight;
	}

	public void setSunlight(double sunlight)
	{
		this.sunlight = sunlight;
		this.sunlightTimestamp = System.currentTimeMillis();
	}
	
	public long getSunlightTimestamp()
	{
		return sunlightTimestamp;
	}

	public FlowerPowerMetadata getMetadata()
	{
		return metadata;
	}
	
	
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowerPower other = (FlowerPower) obj;
		if (metadata == null)
		{
			if (other.metadata != null)
				return false;
		}
		else if (!metadata.equals(other.metadata))
			return false;
		return true;
	}

	public String toString()
	{
		return "FlowerPower [batteryLevel=" + batteryLevel + ", temperature=" + temperature + ", soilMoisture=" + soilMoisture + ", sunlight=" + sunlight + "]";
	}
}
