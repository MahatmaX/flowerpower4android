package de.fp4a.service;



public interface IFlowerPowerServiceManager
{
	public void bind();
	public void unbind();
	
	public void connect();
	public void disconnect();
	public boolean isEnabled();
	public boolean isConnected();
	
	public void pause();
	
	public void enablePersistency(long period, int maxListSize, String storageLocation, String seriesId);
	public void disablePersistency();
	
	public void enableAutoConnect(long period);
	public void disableAutoConnect();
	
	public void addServiceListener(IFlowerPowerServiceListener listener);
	public void removeServiceListener(IFlowerPowerServiceListener listener);
	
	public void destroy();
}
