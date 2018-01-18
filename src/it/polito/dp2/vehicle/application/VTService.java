package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.State;
import it.polito.dp2.vehicle.model.Vehicle;

public class VTService {
//TODO make this a persistence class
	
	private ConcurrentSkipListMap<BigInteger, Vehicle> vehicles;
	private Model model;
	private GraphApp graphApp;
	private BigInteger currVehicleIndex;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	
	// Singleton PATTERN
	private static VTService istance;

	private VTService() {
		vehicles = new ConcurrentSkipListMap<>();
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
		
		//Then add the vehicle to the systems only if present in the model
		if(model.getVehicles()!=null) {
			List<Vehicle> vehicles = model.getVehicles().getVehicle();
			Vehicle v;
			for (int i =0; i< vehicles.size(); i++) {
				v = vehicles.get(i);
				//Supposing valid model, v must have an ID
				this.vehicles.put(v.getID(), v);
				if(currVehicleIndex.compareTo(v.getID()) <= 0) {
					//update current index to the highest value
					currVehicleIndex = v.getID().add(BigInteger.ONE);
				}
				graphApp.addVehicle(v);
			}
		}
	}
	
	public List<Vehicle> getVehicles(){
		List<Vehicle> vs = new ArrayList<>();
		for(Vehicle v : vehicles.values()) {
			vs.add(v);
		}
		return vs;
	}
	
	public List<Vehicle> getVehiclesFromNode(String node) {	
		return graphApp.getVehicles(node);
	}

	public synchronized Vehicle createVehicle(Vehicle v) {
		//TODO There is a better way to avoid race condition?
		//ACTUALLY I DO NOT CHECK IF THE ENTRY POINT IS A ROUTE AND IS ENDPOINT, should I?
		if(v.getCurrentPosition()==null || v.getDestination() == null || v.getPlateNumber() == null) {
			//anyway, if Vehicle is validated, this code is never executed
			logger.log(Level.WARNING, "The vehicle entered has no required information [BAD REQUEST]");
			throw new BadRequestException();
		}
		Vehicle nv = new Vehicle();
		//copy NodeRef
		NodeRef nr = new NodeRef();
		nr.setNode(v.getCurrentPosition().getNode());
		nr.setPort(v.getCurrentPosition().getPort());
		nv.setCurrentPosition(nr);
		//copy Dest
		nv.setDestination(v.getDestination());
		//copy Plate
		nv.setPlateNumber(v.getPlateNumber());
		
		//GET THE PATH from GraphApp and set to vehicle;
		Path p = graphApp.getPath(v.getCurrentPosition(), v.getDestination());
		//if path is null, vehicle is not allowed to enter the system, otherwise it can
		if(p != GraphApp.NO_PATH) {
			//request can be accepted and the vehicle can enter the system
			nv.setPath(p);
			//set in transit state
			nv.setState(State.TRANSIT);
			//SET ID and increment vehicle count
			BigInteger index = currVehicleIndex;
			currVehicleIndex = currVehicleIndex.add(BigInteger.ONE);
			nv.setID(index);
			//SET ENTRY TIME and last update;
			XMLGregorianCalendar xmlGC = getXMLGregorianCalendarNow();
			nv.setEntryTime(xmlGC);
			nv.setLastUpdate(xmlGC);
			//save into the map
			vehicles.put(index, nv);
			//add to the graph
			graphApp.addVehicle(nv);
			return nv;
		}
		else {
			throw new ForbiddenException("There is no path acceptable between requested nodes.");
		}
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