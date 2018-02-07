package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.State;
import it.polito.dp2.vehicle.model.Vehicle;

public class VehicleApp {

	private Vehicle vehicle;
	private NodeApp position;
	private NodeApp destination;
	private PathApp path;
	private GraphApp graphApp;
	
	public VehicleApp(Vehicle v, BigInteger index, GraphApp ga){
		this(v, index, ga, true);
	}
	
	public VehicleApp(Vehicle v, BigInteger index, GraphApp ga, boolean computePath){
		vehicle = new Vehicle();
		graphApp = ga;
		
		//checks the starting node (and its port) and destination node really exists
		position = graphApp.getNode(v.getCurrentPosition().getNode());
		destination = graphApp.getNode(v.getDestination());
		if(position==null || destination==null || !position.containsPort(v.getCurrentPosition().getPort())) {
			throw new BadRequestException();
		}

		//setting the plate of the model
		//PLATE
		vehicle.setPlateNumber(v.getPlateNumber());
		
		//try to add the vehicle in the system. If it cannot be added (constraint not respected) it will return false
		if(!position.addVehicle(this)) {
			throw new ForbiddenException("The vehicle cannot be addded");
		}	
		
		//setting the fields of the model
		//INDEX
		vehicle.setID(index);
	
		//POSITIONS
		//Current
		NodeRef nr = new NodeRef();
		nr.setNode(v.getCurrentPosition().getNode());
		nr.setPort(v.getCurrentPosition().getPort());
		vehicle.setCurrentPosition(nr);
		//Destination
		vehicle.setDestination(v.getDestination());

		//TIMES
		XMLGregorianCalendar xmlGC = getXMLGregorianCalendarNow();
		vehicle.setEntryTime(xmlGC);
		vehicle.setLastUpdate(xmlGC);

		if(computePath) {
			//GET THE PATH from GraphApp;
			PathApp p = graphApp.getPath(position, destination);
			//if path is null, vehicle is not allowed to enter the system, otherwise it can
			if(p != GraphApp.NO_PATH) {
				//request can be accepted and the vehicle can enter the system
				setPath(p);
				//set in transit state	
				vehicle.setState(State.TRANSIT);
			}
			else {
				throw new ForbiddenException("There is no path acceptable between requested nodes.");
			}
		}
		else {
			path = null;
			vehicle.setState(State.PARKED);
		}
		
	}

	public void updatePosition(NodeApp position) {
		this.position = position;
	}
	
	public void setDestination(NodeApp destination) {
		this.destination = destination;
	}
	
	public Vehicle getVehicle() {
		//convert the vehicle as the model needs
		return vehicle;
	}

	public NodeApp getPosition() {
		return position;
	}

	public NodeApp getDestination() {
		return destination;
	}

	public PathApp getPath() {
		return path;
	}

	public void setPath(PathApp path) {
		vehicle.setPath(path.getPath());
		this.path = path;
	}
	
	public BigInteger getID() {
		return vehicle.getID();
	}
	
	private XMLGregorianCalendar getXMLGregorianCalendarNow() 
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory;
        XMLGregorianCalendar now;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
			now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			// It is quite impossible to enter here
			e.printStackTrace();
			now = null;
		}
        
        return now;
    }

	public String getPlateNumber() {
		return vehicle.getPlateNumber();
	}



	
	
}
