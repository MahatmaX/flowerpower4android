package de.fp4a.service;

import java.util.Iterator;
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
		// ToDo: do not enqueue tasks twice and upon reconnection check queue
		// ToDo: check, somehow queue-size exceeded 30 jobs (how?) and resume has not been called upon reconnect (if out of reach).
		Log.d(FlowerPowerConstants.TAG, "Queue-Size= " + queue.size() + " Enqueue " + chara);
		
		QueueJob job = new QueueJob(chara, QueueJob.JOB_TYPE_READ);
		if (!isContained(job))
			queue.offer(job);
		
		if (queue.size() == 1) // if that's the only element in the queue we can immediately start to read
			service.readCharacteristic(chara);
	}
	
	public synchronized void enqueueNotify(BluetoothGattCharacteristic chara, boolean enable)
	{
		Log.d(FlowerPowerConstants.TAG, "Queue-Size= " + queue.size() + " Notify " + chara + " enable");
		
		QueueJob job = new QueueJob(chara, QueueJob.JOB_TYPE_NOTIFY, enable);
		if (!isContained(job))
			queue.offer(job);
		
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
		
			resume(); // process further jobs (if any)
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
		
			resume(); // process further jobs (if any)
		}
		else
			Log.w(FlowerPowerConstants.TAG, "Attention: queue order corrupted ! chara=" + chara.getUuid() + " queue="+ queue);
	}
	
	public void resume()
	{
		if (queue.size() > 0) // execute one more if 'jobs' are contained in the queue
		{
			Log.i(FlowerPowerConstants.TAG, "Queue: process further ");
			
			QueueJob head = queue.peek(); // new head
			
			if (head.jobType == QueueJob.JOB_TYPE_READ)
				service.readCharacteristic(head.chara);
			else if (head.jobType == QueueJob.JOB_TYPE_NOTIFY)
				service.notify(head.chara, head.enable);
		}
	}
	
	private boolean isContained(QueueJob job)
	{
		Iterator<QueueJob> iter = queue.iterator();
		while (iter.hasNext())
		{
			if (job.equals(iter.next()))
				return true;
		}
		return false;
	}
	
	class QueueJob
	{
		final static int JOB_TYPE_READ = 0;
		final static int JOB_TYPE_NOTIFY = 1;
		
		BluetoothGattCharacteristic chara;
		int jobType;
		boolean enable; // indicates if a notification shall be enabled or disabled
		
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
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((chara == null) ? 0 : chara.hashCode());
			result = prime * result + (enable ? 1231 : 1237);
			result = prime * result + jobType;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QueueJob other = (QueueJob) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (chara == null)
			{
				if (other.chara != null)
					return false;
			}
			else if (!chara.getUuid().equals(other.chara.getUuid()))
				return false;
			if (enable != other.enable)
				return false;
			if (jobType != other.jobType)
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			return "QueueJob [chara=" + (chara == null ? null : chara.getUuid()) + ", jobType=" + jobType + "]";
		}

		private FlowerPowerServiceQueue getOuterType()
		{
			return FlowerPowerServiceQueue.this;
		}
	}
}
