package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.ws.rs.BadRequestException;
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
	
	VehicleApp(Vehicle v, GraphApp ga){
		vehicle = new Vehicle();
		this.graphApp = ga;
		
		
		position = graphApp.getNode(v.getCurrentPosition().getNode());
		destination = graphApp.getNode(v.getDestination());
		if(position==null || destination==null || !position.containsPort(v.getCurrentPosition().getPort())) {
			throw new BadRequestException();
		}
		NodeRef nr = new NodeRef();
		nr.setNode(v.getCurrentPosition().getNode());
		nr.setPort(v.getCurrentPosition().getPort());
		vehicle.setCurrentPosition(nr);
		//copy Dest
		vehicle.setDestination(v.getDestination());
		//copy Plate
		vehicle.setPlateNumber(v.getPlateNumber());
		XMLGregorianCalendar xmlGC = getXMLGregorianCalendarNow();
		vehicle.setEntryTime(xmlGC);
		vehicle.setLastUpdate(xmlGC);
		
		position.addVehicle(this);
	}

	public Vehicle getVehicle() {
		//TODO convert the vehicle as the model needs
		return vehicle;
	}

	public NodeApp getPosition() {
		return position;
	}

	public void setPosition(NodeApp position) {
		this.position = position;
	}

	public NodeApp getDestination() {
		return destination;
	}

	public void setDestination(NodeApp destination) {
		this.destination = destination;
	}

	public PathApp getPath() {
		return path;
	}

	public void setPath(PathApp path) {
		this.path = path;
	}
	
	public void setState(State state) {
		vehicle.setState(state);
	}
	
	public BigInteger getID() {
		return vehicle.getID();
	}
	
	public void setID(BigInteger index) {
		vehicle.setID(index);
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
