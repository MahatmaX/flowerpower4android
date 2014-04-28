<?
	require("include/header.php");
?>

<table border="0" width="1000">
	<tr>
		<td valign="top" width="700">
		
			<h2>Class Diagram</h2>
			This class digram depicts the most important classes to be used by developers.
			<p>
			<img src="images/class_diagram.png">
			<p>
			As you can see, there are 3 interfaces (<emph>IFlowerPowerSensor</emph>, <emph>IFlowerPowerServiceManager</emph>, <emph>IFlowerPowerServiceListener</emph>) with which a client application interacts as well as 2 classes realizing the data model (<emph>FlowerPower</emph>, <emph>FlowerPowerMetadata</emph>).
			All of the other classes are hidden for the developer and are only of interest to those wanting to extend the library itself.
			<p>
			<b>IFlowerPowerServiceManager</b>: This class handles the management of individual flower power instances. You mainly use it to bind (and unbind) the communication service and to connect (and disconnect) a flower power sensor. It provides method which must be called upon specific lifecycle stages of your application (see <a href="life_cycle.php">Lifecycle</a>).
			Moreover, you need to register your application as an <b>IFlowerPowerServiceListener</b> with the manager. As such a listener, you'll receive lifecycle events, such as when the communication service is ready (or not), when the flower power sensor is connected (or disconnects) and most importantly when data is available.
			In order to receive such events, your application needs provide implementations for all those methods (see the example, that ships with the distribution).
			<p>
			Once a Flower Power sensor is connected, you probably want to read some values or be notified periodically about updated values. The <b>IFlowerPowerSensor</b> is the be used for that. It provides methods for triggering reads of all kinds of values and for enabling (and disabling) notifications (i.e. periodic reads).
			Note: all these methods return noting, because reads and notifications are executed asynchronously. Once a result is available, it is passed via the IFlowerPowerListener callback (see above) to your application.
			<p>
			This <emph>dataAvailable</emph> callback carries an instance of the <b>FlowerPower</b> data model. This data model provides methods to access all sensor readings as well as timestamps. Note: the model is reused throughout the whole service lifetime, so if e.g. you read the sensor's friendly name once, you do not need to read it again, because it is still set in the Flower Power instance.
			<p>
			Finally, the <b>FlowerPowerService</b> realizes most of the Bluetooth LE handling. It connects via Bluetooth to your Flower Power, it offers methods to read characteristics and to handle notifications. Internally, it is realized as a job queue, so commands are executed sequentially in order not to overwrite previously issues commands. But these internals are only of interest to those who want to work on the library's internals.
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