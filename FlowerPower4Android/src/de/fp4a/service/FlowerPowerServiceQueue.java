package de.fp4a.service;

import java.util.LinkedList;
import java.util.Queue;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import de.fp4a.util.FlowerPowerConstants;

public class FlowerPowerServiceQueue
{

	private final FlowerPowerService service;
	private final Queue<QueueJob> queue;
	
	public FlowerPowerServiceQueue(FlowerPowerService service)
	{
		this.queue = new LinkedList<QueueJob>();
		this.service = service;
	}
	
	public synchronized void enqueueRead(BluetoothGattCharacteristic chara)
	{
		queue.offer(new QueueJob(chara, QueueJob.JOB_TYPE_READ));
		if (queue.size() == 1) // if that's the only element in the queue we can immediately start to read
			service.readCharacteristic(chara);
	}
	
	public synchronized void enqueueNotify(BluetoothGattCharacteristic chara, boolean enable)
	{
		queue.offer(new QueueJob(chara, QueueJob.JOB_TYPE_NOTIFY, enable));
		if (queue.size() == 1) // if that's the only element in the queue we can immediately start to register notify
			service.notify(chara, enable);
	}
	
	public synchronized void dequeueRead(BluetoothGattCharacteristic chara)
	{
		// only remove if this characteristic is the one that was expected to be read.
		QueueJob head = queue.peek();
		if ((head != null) && (head.chara.getUuid().equals(chara.getUuid())))
		{
			queue.remove();
		
			if (queue.size() > 0) // execute one more if 'jobs' are contained in the queue
			{
				head = queue.peek(); // new head
				
				if (head.jobType == QueueJob.JOB_TYPE_READ)
					service.readCharacteristic(head.chara);
				else if (head.jobType == QueueJob.JOB_TYPE_NOTIFY)
					service.notify(head.chara, head.enable);
			}
		}
		else
			Log.w(FlowerPowerConstants.TAG, "Attention: queue order corrupted ! chara=" + chara.getUuid() + " queue="+ queue);
	}
	
	public synchronized void dequeueNotify(BluetoothGattCharacteristic chara)
	{
		// only remove if this characteristic is the one that was expected to be read.
		QueueJob head = queue.peek();
		boolean isLiveMode = chara.getUuid().toString().equals(FlowerPowerConstants.CHARACTERISTIC_UUID_LIVE_MODE);
		
		if ((head != null) && isLiveMode && (head.jobType == QueueJob.JOB_TYPE_NOTIFY))
		{
			Log.i(FlowerPowerConstants.TAG, "Queue: dequeue head");
			queue.remove();
		
			if (queue.size() > 0) // execute one more if 'jobs' are contained in the queue
			{
				Log.i(FlowerPowerConstants.TAG, "Queue: process further ");
				
				head = queue.peek(); // new head
				
				if (head.jobType == QueueJob.JOB_TYPE_READ)
					service.readCharacteristic(head.chara);
				else if (head.jobType == QueueJob.JOB_TYPE_NOTIFY)
					service.notify(head.chara, head.enable);
			}
		}
		else
			Log.w(FlowerPowerConstants.TAG, "Attention: queue order corrupted ! chara=" + chara.getUuid() + " queue="+ queue);
	}
	
	class QueueJob
	{
		final static int JOB_TYPE_READ = 0;
		final static int JOB_TYPE_NOTIFY = 1;
		
		BluetoothGattCharacteristic chara;
		int jobType;
		boolean enable;
		
		public QueueJob(BluetoothGattCharacteristic chara, int jobType)
		{
			this.chara = chara;
			this.jobType = jobType;
		}
		
		public QueueJob(BluetoothGattCharacteristic chara, int jobType, boolean enable)
		{
			this(chara, jobType);
			this.enable = enable;
		}

		@Override
		public String toString()
		{
			return "QueueJob [chara=" + (chara == null ? null : chara.getUuid()) + ", jobType=" + jobType + "]";
		}
	}
}
