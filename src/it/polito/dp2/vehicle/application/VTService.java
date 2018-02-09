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
import javax.ws.rs.NotFoundException;
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
		vehicles = new ConcurrentSkipListMap<>();
		graphApp = new GraphApp(model.getGraph());
		
		//Then add the vehicle to the systems only if present in the model
		if(model.getVehicles()!=null ) {
			List<Vehicle> vehicles = model.getVehicles().getVehicle();
			Vehicle v;
			for (int i =0; i< vehicles.size(); i++) {
				v = vehicles.get(i);
				//Supposing valid model, v must have an ID, valid current position, valid destination
				VehicleApp vapp = new VehicleApp(v, v.getID(), graphApp, false);
				this.vehicles.put(v.getID(), vapp);
				
				if(currVehicleIndex.compareTo(v.getID()) <= 0) {
					//update current index to the highest value
					currVehicleIndex = v.getID().add(BigInteger.ONE);
				}
			}
		}
	}
	
	public Model getModel() {
		//TODO recompute the model including all the vehicles
		return model;
	}
	
	public Graph getGraph() {
		return graphApp.getGraph();
	}
	
	/*
	 * Returns the list of all vehicles in the system.
	 * To do not allow the end-user to modify the list, we copy it, without giving the reference to our map.
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
	 * Return a vehicle given its ID
	 * 
	 */
	public Vehicle getVehicle(BigInteger id) {
		if(vehicles.containsKey(id)) {
			return vehicles.get(id).getVehicle();
		}
		else {
			throw new NotFoundException();
		}
	}
	
	/*
	 * Allow to add a new Vehicle in the system.
	 * First are checked if it has the required information (if validated, it must have) and then, if a path exist, create the vehicle and add it to the system
	 * Then the new vehicle is returned
	 * This function can raise BadRequest exception and Forbidden exception.
	 */
	public Vehicle createVehicle(Vehicle v) {
		//TODO ACTUALLY I DO NOT CHECK IF THE ENTRY POINT IS A ROUTE AND IS ENDPOINT, should I?
		
		if(v==null || v.getCurrentPosition()==null || v.getDestination() == null || v.getPlateNumber() == null) {
			//anyway, if Vehicle is validated, this code is never executed
			logger.log(Level.WARNING, "The vehicle does not have required information [BAD REQUEST]");
			throw new BadRequestException();
		}
		
		VehicleApp nv;
		BigInteger index = currVehicleIndex;
		try {
		 nv =  new VehicleApp(v, index, graphApp);
		}
		catch (BadRequestException bre) {
			logger.log(Level.WARNING, "The vehicle has wrong references [BAD REQUEST]");
			throw bre;
		}
		catch (ForbiddenException bre) {
			logger.log(Level.WARNING, bre.getMessage() + " [FORBIDDEN]");
			throw bre;
		}
		
		currVehicleIndex = currVehicleIndex.add(BigInteger.ONE);
		vehicles.put(index, nv);
		return nv.getVehicle();
	}

	/*
	 * Given the node's id where the vehicle is, 
	 * 	  if the node is in the path: 
	 *  	return true meaning vehicle unchanged, 
	 *    otherwise 
	 *      return false meaning the vehicle has new path 
	 */
	public boolean updateVehicle(BigInteger vid, String nid) {
		NodeApp nap = graphApp.getNode(nid);
		VehicleApp vap = vehicles.get(vid);
		if(nap == null || vap == null) {
			logger.log(Level.WARNING, "The vehicle or the node cannot be found [NOT FOUND]");
			throw new NotFoundException();
		}
		
		return vap.updatePosition(nap);
		
	}
	

}