Flower Power for Android
===============================================

... an open source library for Parrot's Flower Power sensor platform..

It's main features are:

* Easy to use framework to control the Flower Power
* Framework is 100% pure Java and easily extendable
* Transparent access to sensor readings (without having to know Bluetooth LE specifics)
  - SystemId, ModelNr, SerialNr, Firmware/Hardware/Software Revision, Manufacturer Name, Certificate Data, PnP-Id, Friendly Name, Color
  - Readings AND Notifications for Temperature, Soil Moisture, Sunlight, Battery Level
* Advanced Persistency Options
  - Save time series with individual IDs (e.g. for every plant in contrast to every Flower Power sensor)
  - Storage options: internal storage, SD card or Android's SQlite database
  - Automatically smooth and compact time series by applying reduction algorithms (e.g. Douglas-Peucker-Ramer)
* Plot Support
  - Draw individual time series on nice plots
  - Ready-to-use plot fragment for easy UI integration

For further information, documentation and tutorials please see
http://vsis-www.informatik.uni-hamburg.de/oldServer/teaching//projects/flowerpower4android/index.php

Never worked with Github before ?
http://wiki.eclipse.org/EGit/User_Guide#Getting_Started