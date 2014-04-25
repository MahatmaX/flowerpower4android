package de.fp4a.model;

public interface FlowerPowerListener
{

	public void batteryChanged(int batteryLevel);
	public void temperatureChanged(double temperature);
	public void soilMoistureChanged(double soilMoisture);
	public void sunlightChanged(double sunlight);
	
}
