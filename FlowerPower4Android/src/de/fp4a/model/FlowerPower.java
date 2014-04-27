package de.fp4a.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FlowerPower implements Serializable
{
	public enum FlowerPowerColors {UNKNOWN, BROWN, BLUE, GREEN};
	
	// ToDo: Timestamps for every value
	
	// static fields, these normally do not change
	private String systemId 		= "";
	private String modelNr 			= "";
	private String serialNr 		= "";
	private String firmwareRevision	= "";
	private String hardwareRevision	= "";
	private String softwareRevision	= "";
	private String manufacturerName	= "";
	private String certData 		= ""; // IEEE 11073-20601 regulatory certification data list
	private String pnpId 			= "";
	private String friendlyName 	= "";
	private FlowerPowerColors color	= FlowerPowerColors.UNKNOWN; // one of enum 'FlowerPowerColors'
	
	// variable fields, these normally do change over time
	private int batteryLevel 	= -1;
	private double temperature	= -1;
	private double soilMoisture	= -1;
	private double sunlight 	= -1;
	
	// listeners get notified upon updated variable values (see above)
	private ArrayList<FlowerPowerListener> listener;
	
	public FlowerPower()
	{
		listener = new ArrayList<FlowerPowerListener>();
	}

	public String getSystemId()
	{
		return systemId;
	}

	public void setSystemId(String systemId)
	{
		this.systemId = systemId;
	}

	public String getModelNr()
	{
		return modelNr;
	}

	public void setModelNr(String modelNr)
	{
		this.modelNr = modelNr;
	}

	public String getSerialNr()
	{
		return serialNr;
	}

	public void setSerialNr(String serialNr)
	{
		this.serialNr = serialNr;
	}

	public String getFirmwareRevision()
	{
		return firmwareRevision;
	}

	public void setFirmwareRevision(String firmwareRevision)
	{
		this.firmwareRevision = firmwareRevision;
	}

	public String getHardwareRevision()
	{
		return hardwareRevision;
	}

	public void setHardwareRevision(String hardwareRevision)
	{
		this.hardwareRevision = hardwareRevision;
	}

	public String getSoftwareRevision()
	{
		return softwareRevision;
	}

	public void setSoftwareRevision(String softwareRevision)
	{
		this.softwareRevision = softwareRevision;
	}

	public String getManufacturerName()
	{
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
	}

	public String getCertData()
	{
		return certData;
	}

	public void setCertData(String certData)
	{
		this.certData = certData;
	}

	public String getPnpId()
	{
		return pnpId;
	}

	public void setPnpId(String pnpId)
	{
		this.pnpId = pnpId;
	}

	public String getFriendlyName()
	{
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
	}

	public FlowerPowerColors getColor()
	{
		return color;
	}

	public void setColor(FlowerPowerColors color)
	{
		this.color = color;
	}

	public int getBatteryLevel()
	{
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel)
	{
		this.batteryLevel = batteryLevel;
		for (int i=0; i < listener.size(); i++)
			listener.get(i).batteryChanged(batteryLevel);
	}

	public double getTemperature()
	{
		return temperature;
	}

	public void setTemperature(double temperature)
	{
		this.temperature = temperature;
		for (int i=0; i < listener.size(); i++)
			listener.get(i).temperatureChanged(temperature);
	}

	public double getSoilMoisture()
	{
		return soilMoisture;
	}

	public void setSoilMoisture(double soilMoisture)
	{
		this.soilMoisture = soilMoisture;
		for (int i=0; i < listener.size(); i++)
			listener.get(i).soilMoistureChanged(soilMoisture);
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
		for (int i=0; i < listener.size(); i++)
			listener.get(i).sunlightChanged(sunlight);
	}

	public void addListener(FlowerPowerListener listener)
	{
		if (!this.listener.contains(listener))
			this.listener.add(listener);
	}
	
	public void removeListener(FlowerPowerListener listener)
	{
		if (this.listener.contains(listener))
			this.listener.remove(listener);
	}
	
	public ArrayList<FlowerPowerListener> getListener()
	{
		return listener;
	}

	public void setListener(ArrayList<FlowerPowerListener> listener)
	{
		this.listener = listener;
	}

	@Override
	public String toString()
	{
		return "FlowerPower [systemId=" + systemId + ", modelNr=" + modelNr + ", serialNr=" + serialNr + ", firmwareRevision=" + firmwareRevision + ", hardwareRevision=" + hardwareRevision + ", softwareRevision=" + softwareRevision + ", manufacturerName=" + manufacturerName + ", certData=" + certData + ", pnpId=" + pnpId + ", friendlyName=" + friendlyName + ", color=" + color + ", batteryLevel=" + batteryLevel + ", temperature=" + temperature + ", soilMoisture=" + soilMoisture + ", sunlight=" + sunlight + "]";
	}
	
	
}
