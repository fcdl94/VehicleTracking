package it.polito.dp2.vehicle.application;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.State;
import it.polito.dp2.vehicle.model.Vehicle;

public class VTService {

	private ConcurrentSkipListMap<BigInteger, VehicleApp> vehicles;
	private Model model;
	private GraphApp graphApp;
	private BigInteger currVehicleIndex;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	
	// Singleton PATTERN
	private static VTService istance = new VTService();

	private VTService() {
		vehicles = new ConcurrentSkipListMap<>();
		currVehicleIndex = BigInteger.valueOf(0);
		
		InputStream fsr = null;
		InputStream schemaStream = null;
		try {				
			fsr = VTService.class.getResourceAsStream("/xml/xml-gen.xml");
			if (fsr == null) {
				logger.log(Level.SEVERE, "xml directory file Not found.");
				throw new IOException();
			}
			schemaStream = VTService.class.getResourceAsStream("/xsd/vehicleTracking.xsd");
			if (schemaStream == null) {
				logger.log(Level.SEVERE, "xml schema file Not found.");
				throw new IOException();
			}
            JAXBContext jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new StreamSource(schemaStream));
            u.setSchema(schema);
            @SuppressWarnings("unchecked")
			JAXBElement<Model> element = (JAXBElement<Model>) u.unmarshal( fsr );
            
            Model model = element.getValue();
            if (model!=null) {
            	setModel(model);
            }
            fsr.close();
			logger.info("VehicleTracking Initialization Completed Successfully");

		} catch (SAXException | JAXBException | IOException se) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Working with no data.", se);
		} finally {
				try {
					if (fsr!=null)
						fsr.close();
					if (schemaStream!=null)
						schemaStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
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
		if(model.getVehicles()!=null ) {
			List<Vehicle> vehicles = model.getVehicles().getVehicle();
			Vehicle v;
			for (int i =0; i< vehicles.size(); i++) {
				v = vehicles.get(i);
				//Supposing valid model, v must have an ID, valid current position, valid destination
				VehicleApp vapp = new VehicleApp(v, graphApp);
				this.vehicles.put(v.getID(), vapp);
				
				if(currVehicleIndex.compareTo(v.getID()) <= 0) {
					//update current index to the highest value
					currVehicleIndex = v.getID().add(BigInteger.ONE);
				}
			}
		}
	}
	public Model getModel() {
		return model;
	}
	
	public Graph getGraph() {
		return graphApp.getGraph();
	}
	
	/*
	 * Returns the list of all vehicles in the system.
	 * To allow the end-user to modify the list, we copy it, without giving the reference to our map.
	 * 
	 */
	public List<Vehicle> getVehicles(){
		List<Vehicle> vs = new ArrayList<>();
		for(VehicleApp v : vehicles.values()) {
			vs.add(v.getVehicle());
		}
		return vs;
	}
	
	/*
	 * Get vehicles in one node
	 */
	public List<Vehicle> getVehiclesFromNode(String node) {	
		LinkedList<Vehicle> vcs = new LinkedList<>();
		for(VehicleApp va :  graphApp.getVehicles(node)) {
			vcs.add(va.getVehicle());
		}
		return vcs;
	}

	/*
	 * Allow to add a new Vehicle in the system.
	 * First are checked if it has the required information (if validated, it must have) and then, if a path exist, create the vehicle and add it to the system
	 * Then the new vehicle is returned
	 * This function can raise BadRequest exception and Forbidden exception.
	 */
	public Vehicle createVehicle(Vehicle v) {
		//TODO ACTUALLY I DO NOT CHECK IF THE ENTRY POINT IS A ROUTE AND IS ENDPOINT, should I?
		if(v.getCurrentPosition()==null || v.getCurrentPosition().getNode() == null || 
				v.getCurrentPosition().getPort() == null || v.getDestination() == null || v.getPlateNumber() == null) {
			//anyway, if Vehicle is validated, this code is never executed
			logger.log(Level.WARNING, "The vehicle does not have required information [BAD REQUEST]");
			throw new BadRequestException();
		}
		
		VehicleApp nv;
		try {
		 nv =  new VehicleApp(v, graphApp);
		}
		catch (BadRequestException bre) {
			logger.log(Level.WARNING, "The vehicle has wrong references [BAD REQUEST]");
			throw bre;
		}
		
		//GET THE PATH from GraphApp and set to vehicle;
		PathApp p = graphApp.getPath(v.getCurrentPosition(), v.getDestination());
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

			//save into the map
			vehicles.put(index, nv);
			//add to the graph and decrement the future vehicles
			//TODO graphApp.addVehicle(nv);
			
			return nv.getVehicle();
		}
		else {
			throw new ForbiddenException("There is no path acceptable between requested nodes.");
		}
	}


}