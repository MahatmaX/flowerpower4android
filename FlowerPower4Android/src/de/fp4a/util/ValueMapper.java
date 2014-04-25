package de.fp4a.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import de.fp4a.R;
import de.fp4a.R.raw;

import android.content.Context;

public class ValueMapper
{
	private static ValueMapper instance;
	private JSONObject temperatureJSON;
	private JSONObject sunlightJSON;
	private JSONObject soilMoistureJSON;
	
	private ValueMapper(Context c)
	{
		temperatureJSON = readJSON(c, R.raw.temperature);
		sunlightJSON = readJSON(c, R.raw.sunlight);
		soilMoistureJSON = readJSON(c, R.raw.soilmoisture);
	}
	
	private JSONObject readJSON(Context c, int id)
	{
		InputStream is = c.getResources().openRawResource(id);
		
		try
		{
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
		    StringBuilder responseStrBuilder = new StringBuilder();
	
		    String inputStr;
		    while ((inputStr = streamReader.readLine()) != null)
		        responseStrBuilder.append(inputStr);
		    
		    return new JSONObject(responseStrBuilder.toString());
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
		return null;
	}

	public static ValueMapper getInstance(Context c)
	{
		if (instance == null)
			instance = new ValueMapper(c);
		return instance;
	}
	
	public int mapTemperature(int i)
	{
		if ((i < 210) || (i > 1372)) // this is the minimum/maximum in the corresponding json-map
			return -1;
		 
		try
		{
			return temperatureJSON.getJSONObject("C").getInt(i+"");
		}
		catch (JSONException e)
		{
			return mapTemperature(i-1); // in case the json file does not contain an entry for i, try with the next lower value
		}
	}

	public double mapSunlight(int i)
	{
		if ((i < 0) || (i > 65530)) // this is the minimum/maximum in the corresponding json-map
			return -1;
		
		try
		{
			return sunlightJSON.getDouble(i+"");
		}
		catch (JSONException e)
		{
			return mapSunlight(i-1); // in case the json file does not contain an entry for i, try with the next lower value
		}
	}
	
	public double mapSoilMoisture(int i)
	{
		if ((i < 210) || (i > 700)) // this is the minimum/maximum in the corresponding json-map
			return -1;
		 
		try
		{
			return soilMoistureJSON.getDouble(i+"");
		}
		catch (JSONException e)
		{
			return mapSoilMoisture(i-1); // in case the json file does not contain an entry for i, try with the next lower value
		}
	}
}
