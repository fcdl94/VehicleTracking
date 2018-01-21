# VehicleTracking
DP2 Project of 2017

The aim of this project is to design and implement a RESTful web service that can track the presence of vehicles in an area with restricted access and, based on the tracking information, can provide permission to new vehicles to enter the area and suggestions about the path to their destinations.
The service uses a model of the area that is a graph, with nodes representing roads and parking areas, and arcs representing the possibility to go from one node to another one. When a new vehicle wants to enter the area, it has to ask the service for permission, specifying both the entry point (i.e. its current position) and the desired destination in the area. The service will check if it is possible to allow the entrance of the new vehicle, based on the area model (which can include a number of constraints, such as capacity of parking areas and of roads) and based on the current and expected future positions of the other vehicles in the area. If the service allows entrance, it responds by also indicating a suggested path that the new vehicle is expected to follow from the entry point to the
destination, and a unique identifier assigned by the service to the new vehicle. While the vehicle is moving to its destination it will periodically send information about its current position to the  service, which will update its tracking information. If the service realizes that a vehicle is not following the suggested path, it will compute an updated path that starts from the current vehicle position and will send it back to the vehicle in response to the position tracking message. 
The area model must be specified in an XML document, for which a schema has to be designed. The service must upload the area model from the XML file at startup and it must allow administrators to collect various kinds of information about the vehicles currently in the area and their expected routes.


## IMPORTANT
In order to try the system use the build.xml file and run the compile task (auto generate classes from xsd)
You also must have in /opt/dp2/shared/lib the libraries for JavaRX

## TomCat
In ANT scripts must be configured the location of TOMCAT application (I had troubles with CATALINA_HOME variable)
The to run the TomCat server, open build-proj.xml ANT file and run start-tomcat

## Service
To deploy the service you must open build-proj.xml ANT file and run deployWS