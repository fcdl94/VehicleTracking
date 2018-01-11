package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.State;
import it.polito.dp2.vehicle.model.Vehicle;

public class VTService {

	private HashMap<BigInteger, Vehicle> vehicles;
	private Model model;
	private GraphApp graphApp;
	private BigInteger currVehicleIndex;
	
	// Singleton PATTERN
	private static VTService istance;

	private VTService() {
		vehicles = new HashMap<>();
		currVehicleIndex = BigInteger.valueOf(0);
	}

	public static VTService getVTService() {
		if (istance == null) {
			istance = new VTService();
		}
		return istance;
	}

	/*
	 * This function should be used at startup to load the model in the system
	 */
	public void setModel(Model model) {
		this.model = model;
		
		//FIRST generate the graph
		if (graphApp == null)
			graphApp = new GraphApp(model.getGraph());
		
		//Then add the vehicle to the systems
		List<Vehicle> vehicles = model.getVehicles().getVehicle();
		Vehicle v;
		for (int i =0; i< vehicles.size(); i++) {
			v = vehicles.get(i);
			this.vehicles.put(v.getID(), v);
			if(currVehicleIndex.compareTo(v.getID()) <= 0) {
				//update current index to the highest value
				currVehicleIndex = v.getID().add(BigInteger.ONE);
			}
			graphApp.addVehicle(v);
		}
	}
	
	public List<Vehicle> getVehiclesFromNode(String node) {	
		return graphApp.getVehicles(node);
	}
	
	public boolean createVehicle(Vehicle v) {
		//ACTUALLY I DO NOT CHECK IF THE ENTRY POINT IS A ROUTE AND IS ENDPOINT
		if(v.getCurrentPosition()==null || v.getDestination() == null || v.getPlateNumber() == null) {
			if(System.getenv("DEBUG") != null) {
				System.out.println("ERROR - The vehicle entered has no required information [BAD REQUEST]");
			}
			return false;
		}
		else if ( !graphApp.containsNode(v.getCurrentPosition().getNode(), v.getCurrentPosition().getPort()) ||  !graphApp.containsNode(v.getDestination()) ){
			if(System.getenv("DEBUG") != null) {
				System.out.println("ERROR - The vehicle entered has wrong information [BAD REQUEST]");
			}
			return false;
		}
		else {		
			//GET THE PATH from GraphApp and set to vehicle;
			Path p = graphApp.getPath(v.getCurrentPosition(), v.getDestination());
			if(p != null) {
				//request can be accepted and the vehicle can enter the system
				v.setPath(p);
				//set in transit state
				v.setState(State.TRANSIT);
				//SET ID and increment vehicle count
				BigInteger index = currVehicleIndex;
				currVehicleIndex.add(BigInteger.ONE);
				v.setID(index);
				//SET ENTRY TIME and last update;
				XMLGregorianCalendar xmlGC = getXMLGregorianCalendarNow();
				v.setEntryTime(xmlGC);
				v.setLastUpdate(xmlGC);
				//save into the map
				vehicles.put(index, v);
				//add to the graph
				graphApp.addVehicle(v);
				return true;
			}
			//else return false
		}
		return false;
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
	
}