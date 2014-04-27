package de.fp4a.model;

public interface FlowerPowerListener
{

	Überlegen, ob ich den FlowerPowerListener überhaupt brauche, da ich ja die aktuellen Daten ohnehin über dataAvailable bekomme
	
	public void batteryChanged(int batteryLevel);
	public void temperatureChanged(double temperature);
	public void soilMoistureChanged(double soilMoisture);
	public void sunlightChanged(double sunlight);
	
}
