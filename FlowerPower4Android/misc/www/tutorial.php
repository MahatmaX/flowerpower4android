<?
	require("include/header.php");
?>

<table border="0" width="1000">
	<tr>
		<td valign="top" width="700">
		
			<h2>Tutorial</h2>
			
			In this tutorial you will learn how to use the provided features of <i>Flower Power for Android</i>. We will start with introducing the application and service lifecycle and how to connect to a Flower Power device.
			
			Before we are going to show how to program with the <i>Flower Power for Android</i> library, we want to make a short excursus to Android's application and service lifecycle.
			The main reason for this is the more or less complex connection handling with the Flower Power in order to consume as less energy as possible. Ideally, we want to connect to the Flower Power only, when we require data (e.g. display current state on the UI).
			But sometimes, we also want to keep a connection in order to continuously record data (e.g. to record, store and later on display time series over a certain time interval).
			<p>
			In the following, we will see how to create a minimal Android application that establishes a connection to a Flower Power, requests some data, writes the data into a persistent storage and finally displays the data in a nice plot.
			
			<h3>Preparations</h3>
			
			But, we first need to make some preparations:
			<ul>
				<li>Create an Android project, give it a name, define an application package name, create an empty Activity and call it for example <i>de.fp4a.apps.tutorial.FlowerPowerTutorialActivity</i></li>
				<li>Add the following lines to your Android manifest:<br>
<pre style="background:#EEEEEE">
&lt;uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" /&gt;

&lt;uses-permission android:name="android.permission.BLUETOOTH" /&gt;
&lt;uses-permission android:name="android.permission.BLUETOOTH_ADMIN" /&gt;
</pre>
					These lines state that Bluetooth LE (<i>Bluetooth Low Energy</i> aka <i>Bluetooth 4.0</i> aka <i>Bluetooth Smart</i>) is required (uses feature) and that your app requests two permissions in order to find Bluetooth devices and interact with them. Moreover, add ...
<pre style="background:#EEEEEE">
&lt;activity
    android:name="de.fp4a.gui.DeviceScanActivity"
    android:label="@string/app_name" &gt;
        &lt;intent-filter&gt;
                &lt;action android:name="android.intent.action.MAIN" /&gt;
        &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
    &lt;/intent-filter&gt;
&lt;/activity&gt;
&lt;activity android:name="de.fp4a.apps.tutorial.FlowerPowerTutorialActivity" /&gt;
</pre>
					These lines define two Activities. The first one is our entry point to the application. It is a simple Activity that scans for and displays all nearby Bluetooth devices. Using this Activity the user can pick one Flower Power device which will subsequently be used by the <i>FlowerPowerTutorialActivity</i> to retrieve data.
				</li>
			</ul>
			
			<h3>Foundations of Application and Service Lifecycle</h3>
			
			Now that we have our initial project set up, we can have a look at an Activity's lifecycle. The main callback methods are shown below (you should already be familiar with Android programming as some basics are assumed to be known):
<pre style="background:#EEEEEE">
public class FlowerPowerTutorialActivity extends Activity
{
  /** Called once the Activity is initially created */
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.flowerpower_main);

    Intent intent = getIntent();
    String deviceName    = intent.getStringExtra(
                             FlowerPowerConstants.EXTRAS_DEVICE_NAME);
    String deviceAddress = intent.getStringExtra(
                             FlowerPowerConstants.EXTRAS_DEVICE_ADDRESS);
  }

  /** Called once the Activity is visible after having been paused */
  protected void onResume()
  {
    super.onResume();
  }

  /** Called once the Activity is no longer visible 
      (e.g. because another Activity is now in the foreground) */
  protected void onPause()
  {
    super.onPause();
  }

  /** Called once the Activity is finally being destroyed */
  protected void onDestroy()
  {
    super.onDestroy();
  }
}
</pre>        	
			Looking at the <i>onCreate</i> method we first set a specific layout (you probably want to set yours here) and then get the device's name and its address. In the Intent that triggered the creation of our Activity (the <i>DeviceScanActivity</i> of <i>Flower Power for Android</i> does this) two extra fields containing the name and address of the chosen Bluetooth device are provided. The address is the so called MAC address of the Bluetooth device and is a universally unique identifier. We need it to establish a connection.
			<p>
			The connection is established by an Android service (<i>FlowerPowerService</i>), which runs in the background independently of the Activity. Hence you need to declare this service in your Android manifest:
<pre style="background:#EEEEEE">
&lt;service
    android:name="de.fp4a.service.FlowerPowerService"
    android:enabled="true" /&gt;
</pre>
			The service offers several methods to interact with a Bluetooth device. But in order to transparently access the Flower Power device, you only need to interact with the <i>FlowerPowerServiceManager</i>. This class offers several convenience methods to manage the service's lifecycle. These methods should be called in the corresponding lifecycle callbacks of your activity:

<pre style="background:#EEEEEE">
// in onCreate
serviceManager = FlowerPowerServiceManager.getInstance(deviceAddress, this);
serviceManager.bind();
	
// in onResume
serviceManager.connect();
	
// in onPause
serviceManager.pause();
	
// in onDestroy
serviceManager.disconnect();
serviceManager.unbind(); 
</pre>
			
			In the <i>onCreate</i> method an instance of the serviceManager is created (if it not already exists) and the device address is passed, because each physical Flower Power device is handled by its own service (if you have e.g. three Flower Powers you'll end up with three service instances). Afterwards, the manager is requested to create (if not already done so previously) and to bind the service.
			<p>
			After the onCreate method, the <i>onResume</i> method is called. It is here, when we establish a connection to the device. In the <i>onPause</i> method which is called once the Activity is not visible anymore, we do not disconnect (though we may), but we only pause the receiving of state updates from the service (see below). It is the <i>onDestroy</i> method in which we actually disconnect and unbind the service, releasing all resources hold by the service.
			
			<h3> Reading Data </h3>
			So far, we have seen how to handle the service lifecycle, but we have not yet seen how to actually retrieve some data. Understandably, we can only retrieve data if we successfully (!) connected to the device. But how do we know if we did ? All events that may happen in the service's lifecycle (connection, disconnection, failure, data available etc.) are dispatched to listeners. The <i>IFlowerPowerServiceListener</i> is an interface, which defines callback methods to be implemented by anyone interested in receiving such service status updates. And such a listener needs to be registered with the serviceManager:
<pre style="background:#EEEEEE">
IFlowerPowerServiceListener serviceListener = new IFlowerPowerServiceListener() {
	
  public void deviceConnected() { }
  public void deviceDisconnected() { }
  public void deviceReady(IFlowerPowerDevice device) { }
  public void dataAvailable(FlowerPower fp) { }
  public void serviceConnected() { }
  public void serviceDisconnected() { }                
  public void serviceFailed() {
    finish();
  }
};
serviceManager.addServiceListener(serviceListener);
</pre>
			So, what you have to do is to implement the <i>IFlowerPowerServiceListener</i> interface (above it is implemented as an anonymous inner class) and to simply register the implementing class as a service listener with the serviceManager. Afterwards, the serviceManager will call your class once any events happen. The two most important methods are: <i>deviceReady</i> and <i>dataAvailable</i>. deviceReady is called once a connection to the Flower Power is established successfully and everything is set up in order to start requesting data. In order to do so, an <i>IFlowerPowerDevice</i> is provided as argument. Use this to read data from the Flower Power like this:
<pre style="background:#EEEEEE">
device.readFriendlyName();
device.readBatteryLevel();
device.readTemperature();
device.readSunlight(); 
device.readSoilMoisture();
device.read ...
</pre>
			As you can see, none of these methods has a return value. So, how to actually get the data ? Read requests are executed asynchronously, that means, the control flow returns immediately and once the requested data is available an event is triggered and all service listeners are informed (see above). This is, where the other method, <i>dataAvailable</i>, comes into play. Once new data is available, this method is called providing an instance of <i>FlowerPower</i>. That class represents the data model and holds all information that have been read so far. You can now access the results of your corresponding read requests:
<pre style="background:#EEEEEE">
String friendlyName = fp.getMetadata().getFriendlyName();
int batteryLevel    = fp.getBatteryLevel();
double temperature  = fp.getTemperature();
...
</pre>
			<h3>Notifications</h3>
			
			If you want continuous updates, you do not have to call readXYZ() periodically. For this purpose, you can use notifications. If you subscribe for a notification, the corresponding sensor characteristic is updated automatically once in a second.
			
<pre style="background:#EEEEEE">
device.notifyTemperature(true);
device.notifySunlight(true);
device.notifySoilMoisture(true);
device.notifyBatteryLevel(true);
</pre>

			You can enable notifications for dynamic characteristics, i.e. attributes that change over time. If you call notifyXYZ, you will receive subsequent updates via the <i>dataAvailable</i> method of your listener (see above). Do not forget to disable notifications once you do not use them anymore. Call <i>notifyXYZ(false)</i> to do so.
			
			<h3>Persistency</h3>
			Persisting values allows to create time series over a certain time span. This is useful, e.g. to display the changes graphically or to export time series in order to postprocess these with an external programm (e.g. for statistics). In order to persist time series, you simply call

<pre style="background:#EEEEEE">
serviceManager.enablePersistency(5000, 1000, 
  FlowerPowerConstants.PERSISTENCY_STORAGE_LOCATION_INTERNAL, "flowerpower4android");
</pre>

			This call requires four arguments:
			<ul>
				<li>
					In the example above, the first argument <i>5000</i> specifies the time interval in ms. After 5000 ms all new sensor readings are persisted. 
				</li>
				<li>
					The second argument states the max. number of readings that shall be persisted with one flush. If in the example above the list of readings contains 1001 readings, the time series is first processed using a Douglas-Peucker-Ramer algorithm in order to smooth the series and if that algorithm was not able to reduce the size below 1000 a compacting algorithm is executed which simply deletes every odd-numbered reading. 
				</li>
				<li>
					The third argument is used to specify the location where the time series shall be stored. Possible locations are <i>external</i> (on SD card), <i>internal</i> (in the phones memory) and <i>database</i> (Android's internal SQlite database).
				</li>
				<li>
					Finally, the last argument represents a unique ID for the series. If you provide <i>null</i> the Bluetooth MAC address will be used and all time series are related with the actual Flower Power device. But in case you use your Flower Power to monitor several plants and you move your Flower Power from one plant to another once in a while, then you may want to have time series for individual plants (and not for the device). Hence you can specify an arbitrary name to be used to identify the time series. Note: you do not need to specify different names for different characteristics (e.g. temperature and sunlight). The type of measurement is automatically prefixed.
				</li>
			</ul>
			
			<h3>Displaying Plots</h3>
			Write me !
		</td>
		<td valign="top" width="300">
			<?
				require("include/navigation.php");
			?>
		</td>
	</tr>
</table>

<?
	require("include/footer.php");
?>