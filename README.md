# Chemical-Air-Data
A website designed for viewing the air pollution data for various states in the United States of America from 2020 all the way back to 1985. An easy to use interface with valuable information at your fingertips!

Contains Data on:
##### Carbon Monoxide, Lead, Nitrogen dioxide, Ozone, Particulate matter <= 10μm in diameter, Particulate matter <= 2.5μm in diameter, Sulfur dioxide

Air Pollution Data is Collected from epa.gov website which has many air pollution monitors that data is pulled from.

### Uses:
Tomcat – for the local server

HTML/CSS – for the web pages

Java/Jquery/Javascript - for jsp pages

Java/Apache Commons/JDBC – for database interaction

Java/Selenium – for data collection

Maven – for dependencies


## Who is this data useful for?
- This application would be useful for those who want more information on air pollution statistics in certain states, either for research purposes or to be more well informed on how certain states are doing in regards to pollution.
- Would allow users to see the statistics on a state in an easy to use and view interface to get the data they need fast

## Why is this important?
- Short-lived climate pollutants are powerful climate forcers with global warming potentials many times that of carbon dioxide. 
- They also significantly impact food, water and economic security for large populations throughout the world
- The effects of short-lived climate pollutants represent a major development issue that calls for quick and significant global action. 

## How To Use:
- To Run the Data Collection and Database part, you must open up maven and run the tomcat server then run the MainRunner class
- To Run the Web Applications set up your Tomcat server to run the exploded war
- Go to the address of the exploded war in your browser and you are now at the site
- In the state map select the state you want to look at and then the selector will pop up
- In the selector choose your desired time frame and pollutant and the information will be displayed

## Results:

### Homepage

![](/Air%20Data%20Screenshots/homepage.PNG)

### Pollutant Information

![](/Air%20Data%20Screenshots/pollutantInfo.PNG)

### About Us

![](/Air%20Data%20Screenshots/aboutUs.PNG)

### State Map

![](/Air%20Data%20Screenshots/stateMapBase.PNG)
![](/Air%20Data%20Screenshots/stateMapForme.PNG)
![](/Air%20Data%20Screenshots/stateMapDisplayedInfo.PNG)

### Heat Map

![](/Air%20Data%20Screenshots/heatMapBase.PNG)
![](/Air%20Data%20Screenshots/heatMapBlue.PNG)
![](/Air%20Data%20Screenshots/heatMapRed.PNG)
![](/Air%20Data%20Screenshots/heatMapGreen.PNG)
![](/Air%20Data%20Screenshots/heatMapGreenDisplay.png)
