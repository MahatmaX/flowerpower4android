package de.fp4a.timeseries.dao;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.fp4a.util.FlowerPowerConstants;

public class TimeSeriesModelSQLiteDAO extends SQLiteOpenHelper implements ITimeSeriesModelDAO
{

	private final static String DATABASE_NAME = "timeseries.db";
	private final static int DATABASE_VERSION = 1;
	
	private final static String COLUMN_TIMESTAMP = "timestamp";
	private final static String COLUMN_VALUE = "value";
	
	private String tableName;
	
	public TimeSeriesModelSQLiteDAO(String tableName, Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.tableName = tableName;
	}
	
	public synchronized void load(LinkedList<Long> timestamps, LinkedList<Float> values) throws Exception
	{
		Log.d(FlowerPowerConstants.TAG, "# Measurements before loading: " + timestamps.size() + " measurements ...");
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query(tableName, new String[] {COLUMN_TIMESTAMP, COLUMN_VALUE}, null, null, null, null, COLUMN_TIMESTAMP);
		}
		catch(Exception exc)
		{
			deleteDataFiles();
			throw exc;
		}
		
		int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
		int valueIndex = cursor.getColumnIndex(COLUMN_VALUE);
		
		while (cursor.moveToNext())
		{
//			long time = ;
//			if (timestamps.size() > 0 && time < timestamps.getLast())
//				LogHandler.modelLevel(this, "Attention History not valid !!! " + timestamps.size());
			
			timestamps.addLast(cursor.getLong(timestampIndex));
			values.addLast(cursor.getFloat(valueIndex));
		}
		
		Log.i(FlowerPowerConstants.TAG, "Loaded " + timestamps.size() + " measurements ...");
	}

	public synchronized void save(LinkedList<Long> timestamps, LinkedList<Float> values, boolean append) throws Exception
	{
		if (!append) // if not append, simply delete the database and create a new one
		{
			deleteDataFiles();
		}
		
		Log.i(FlowerPowerConstants.TAG, "Going to save " + timestamps.size() + " measurements ...");
		long startTime = System.currentTimeMillis();
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();

		for (int i=0; i < timestamps.size(); i++)
		{
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_TIMESTAMP, timestamps.get(i));
			cv.put(COLUMN_VALUE, values.get(i));
			
			try
			{
				db.insert(tableName, null, cv);
			} 
			catch(SQLiteConstraintException exc)
			{
				// it is possible, that two entries with the same timestamp shall be inserted !
				// this raises an exception. The reason for this is that upon very high measurement rates, 
				// the service inserts the same values twice in the list of measurements to be persisted.
				// But only if the last value of last set of measurements to be persisted is the same as the first
				// element of the current set.
				// Hence, just ignore this !!!
				Log.d(FlowerPowerConstants.TAG, "ATTENTION !!!" + exc.getMessage());
			}
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
		
		Log.d(FlowerPowerConstants.TAG, "Finished saving ! It took " + (System.currentTimeMillis()-startTime) + " ms"); 
	}
	
	public synchronized void deleteDataFiles()
	{
		getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
		Log.d(FlowerPowerConstants.TAG, "Database table successfully dropped");
		
		// always create a new database independent of whether this DAO is going to be replaced by another DAO 
		onCreate(getWritableDatabase());
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table " + tableName + " (" + COLUMN_TIMESTAMP + " long primary key, " + COLUMN_VALUE + " float);"); // create table
		Log.d(FlowerPowerConstants.TAG, "Database table successfully created");
	} 

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.d(FlowerPowerConstants.TAG, "onUpgrade: ATTENTION ! Should not be called !");
	}

}
