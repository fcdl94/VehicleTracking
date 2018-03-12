package it.polito.dp2.vehicle.application;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

/**
 * This class implements a singleton, application gateway for the Application package.
 * This class represents the Vehicle Tracking service and offers all the method able to handle the vehicles and the model.
 * 
 * @author Fabio Cermelli
 *
 */
public class VTService {

	private ConcurrentSkipListMap<BigInteger, VehicleApp> vehicles;
	private GraphApp graphApp;
	private BigInteger currVehicleIndex;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	
	// Singleton PATTERN
	private static VTService istance = new VTService();

	/**
	 * The constructor tries to build the model from an xml that is found in the WAR package and is called xml-gen.xml.
	 * It the xml cannot be found, the VTservice is made anyway but it is expected that a model will be load with {@linkplain public void setModel(Model model)}.
	 * 
	 */
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

	/**
	 * This function should be used at startup to load the model in the system
	 */
	public void setModel(Model model) {
		//this.model = model;
		vehicles = new ConcurrentSkipListMap<>();
		graphApp = new GraphApp(model.getGraph());
		currVehicleIndex = BigInteger.ZERO;
		
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
		
	public Graph getGraph() {
		return graphApp.getGraph();
	}
	
	/**
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
	
	/**
	 * Get vehicles in one node
	 */
	public List<Vehicle> getVehiclesFromNode(String node) {	
		LinkedList<Vehicle> vcs = new LinkedList<>();
		Set<VehicleApp> vfn = graphApp.getVehicles(node);
		if(vfn==null) {
			throw new NotFoundException();
		}
		for(VehicleApp va :  vfn) {
			vcs.add(va.getVehicle());
		}
		return vcs;
	}

	/**
	 * Return a vehicle given its ID
	 */
	public Vehicle getVehicle(BigInteger id) {
		if(vehicles.containsKey(id)) {
			return vehicles.get(id).getVehicle();
		}
		else {
			throw new NotFoundException();
		}
	}
	
	/**
	 * Allow to add a new Vehicle in the system.
	 * First it is checked if it has the required information (if validated from schema, it must have) and then, create the vehicle and add it to the system
	 *  
	 * This function can raise BadRequest exception and Forbidden exception.
	 *
     * @return the created vehicle model
	 */
	public Vehicle createVehicle(Vehicle v) {
		//TODO ACTUALLY I DO NOT CHECK IF THE ENTRY POINT IS A ROUTE AND IS ENDPOINT, should I?
		
		if(v==null || v.getCurrentPosition()==null || v.getDestination() == null || v.getPlateNumber() == null) {
			//anyway, if Vehicle is validated, this code is never executed
			logger.log(Level.INFO, "The vehicle does not have required information [BAD REQUEST]");
			throw new BadRequestException();
		}
		
		VehicleApp nv;
		BigInteger index;
		synchronized(currVehicleIndex) {
			index = currVehicleIndex;
			currVehicleIndex = currVehicleIndex.add(BigInteger.ONE);
		}
		try {
			nv =  new VehicleApp(v, index, graphApp);
		}
		catch (BadRequestException bre) {
			logger.log(Level.INFO, "The vehicle has wrong references [BAD REQUEST]");
			throw bre;
		}
		catch (ForbiddenException bre) {
			logger.log(Level.INFO, bre.getMessage() + " [FORBIDDEN]");
			throw bre;
		}
		
		vehicles.put(index, nv);
		return nv.getVehicle();
	}

	/**
	 * Given the node's id where the vehicle is, 
	 * 	  if the node is in the path: 
	 *  	return true meaning vehicle unchanged, 
	 *    otherwise 
	 *      return false meaning the vehicle has new path 
	 *      
	 * @param vid BigInteger that represents the ID of the vehicle
	 * @param nid String that represent the node ID of the node where the vehicle is
	 * @return true if the vehicle is in the path, false if a new path has been computed
	 */
	public boolean updateVehiclePosition(BigInteger vid, String nid) {
		NodeApp nap = graphApp.getNode(nid);
		VehicleApp vap = vehicles.get(vid);
		if(nap == null || vap == null) {
			logger.log(Level.INFO, "The vehicle or the node cannot be found [NOT FOUND]");
			throw new NotFoundException();
		}
		
		return vap.updatePosition(nap);
	}
	
	/**
	 * This function is useful to modify the vehicle destination or current position and compute a new path between them
	 * 
	 * @param vid BigInteger that represents the ID of the vehicle
	 * @param v Vehicle that carries the information needed (needed fields are current position and destination)
	 * @return the vehicle model
	 */
	public Vehicle updateVehicle(BigInteger vid, Vehicle v) {
		VehicleApp vap = vehicles.get(vid);
		
		if(v==null || v.getCurrentPosition()==null || v.getDestination() == null || v.getPlateNumber() == null) {
			//anyway, if Vehicle is validated, this code is never executed
			logger.log(Level.INFO, "The vehicle does not have required information [BAD REQUEST]");
			throw new BadRequestException();
		}
		
		if(vap == null) {
			logger.log(Level.INFO, "The vehicle cannot be found [NOT FOUND]");
			throw new NotFoundException();
		}
		
		if( !v.getPlateNumber().equals(vap.getPlateNumber())){
			logger.log(Level.INFO, "The vehicle contains uncoherent information [FORBIDDEN]");
			throw new ForbiddenException();
		}
		
		vap.update(v);
		
		
		return vap.getVehicle();
	}

	/**
	 * The method implements the deleting of a vehicle only if the vehicle is on end-point road.
	 * 
	 */
	public boolean deleteVehicle(BigInteger vid) {
		//The other solution may was that the delete supplies a path to the vehicle to exit the system
		
		VehicleApp vap = vehicles.get(vid);
		
		if(vap==null) {
			throw new NotFoundException();
		}
		
		if(vap.getPosition() instanceof RoadApp) {
			RoadApp rap = (RoadApp) vap.getPosition();
			if(rap.isEndpoint()) {
				vap.remove();
				vehicles.remove(vid); //to keep the vehicle in the system for auditing purposes, comment this method or use another map and add it to that.
				return true;
			}
		}
				
		return false;
	}
	

}