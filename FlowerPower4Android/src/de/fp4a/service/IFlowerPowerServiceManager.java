package de.fp4a.service;



public interface IFlowerPowerServiceManager
{

	public void bind();
	public void unbind();
	
	public void connect();
	public void disconnect();
	public boolean isConnected();
	
	public void pause();
	
	public void enablePersistency(boolean enable, long period);
	
	public void addServiceListener(IFlowerPowerServiceListener listener);
	public void removeServiceListener(IFlowerPowerServiceListener listener);
	
	
}
