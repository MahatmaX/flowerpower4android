<?
	require("include/header.php");
?>

<table border="0" width="1000">
	<tr>
		<td valign="top" width="700">
		
			It's main features are:
			<ul>
				<li> Easy to use framework to control the Flower Power </li>
				<li> Framework is 100% pure Java and easily extendable </li>
				<li> 
					Transparent access to sensor readings (without having to know Bluetooth LE specifics)
					<ul>
						<li>SystemId, ModelNr, SerialNr, Firmware/Hardware/Software Revision, Manufacturer Name, Certificate Data, PnP-Id, Friendly Name, Color</li>
	  					<li>Readings AND Notifications for Temperature, Soil Moisture, Sunlight, Battery Level</li>
					</ul>
				</li>
				<li> 
					Advanced Persistency Options 
					<ul>
						<li>Save time series with individual IDs (e.g. for every plant in contrast to every Flower Power sensor)</li>
						<li>Storage options: internal storage, SD card or Android's SQlite database</li>
						<li>Automatically smooth and compact time series by applying reduction algorithms (e.g. Douglas-Peucker-Ramer)</li>
					</ul>
				</li>
				<li> 
					Plot Support
					<ul>
						<li>Draw individual time series on nice plots</li>
						<li>Ready-to-use plot fragment for easy UI integration</li>
					</ul>
				</li>
			</ul>
			
			<p>
			The library as well as this website are intended for developers. It mainly offers download, documentation and contact information.
			<p>
			<i>Flower Power for Android</i> is still beta and in an early stage of development, but it's open: comments, bug reports and contributions are warmly welcome ! 
			<p>
			Credits go to <i>Sandeep Mistry</i> (for a pioneering <a href="https://github.com/sandeepmistry/node-flower-power" target="_blank">JavaScript library</a>), <i>Moritz (for the first publicly available <a href="https://play.google.com/store/apps/details?id=de.cloudffm.flowerpowerradio&hl=de" target="_blank">Android proof-of-concept</a>)</i> and <i>Jerome</i> (for information and motivation).
			<p>
			<center>
				<img src="images/screenshot.png" alt="Screenshot of the Flower Power for Android Example App">
				<img src="images/screenshot2.png" alt="Screenshot of the Flower Power for Android Example App">
				<br>
				Screenshots of the Example App for the <i>Flower Power for Android</i> Library
			</center>
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