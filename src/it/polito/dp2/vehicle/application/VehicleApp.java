package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.vehicle.model.State;
import it.polito.dp2.vehicle.model.Vehicle;

/**
 * This class represents a vehicle.
 * This class implements methods useful to perform operation on vehicle as the update of the position and of important fields.
 * Also, this class wraps the method of Vehicle model class.
 * 
 * @see {@link Vehicle}
 * @author Fabio Cermelli
 *
 */
public class VehicleApp {

	private static Logger logger = Logger.getLogger(VTService.class.getName());
	private Vehicle vehicle;
	private NodeApp position;
	private NodeApp destination;
	private PathApp path;
	private GraphApp graphApp;
	
	/**
	 * The constructor takes the vehicle model, the index and the GraphApp class and makes a vehicle.
	 * First it checks if the vehicle model is consistent with the graph model.
	 * Then it clone the vehicle model (and does not save a reference to the parameter).
	 * 
	 * In the end, it computes the path of the vehicle and store it.
	 * 
	 * @param v it is the vehicle model
	 * @param index it is the index of the vehicle
	 * @param ga it is the GraphApp of the system
	 */
	public VehicleApp(Vehicle v, BigInteger index, GraphApp ga){
		this(v, index, ga, true);
	}
	
	/**
	 * The constructor takes the vehicle model, the index and the GraphApp class and makes a vehicle. 
	 * First it checks if the vehicle model is consistent with the graph model. 
	 * Then it clone the vehicle model (and does not save a reference to the parameter).
	 * 
	 * In the end, if computePath == true, it computes the path of the vehicle and store it.
	 * 
	 * @param v it is the vehicle model
	 * @param index it is the index of the vehicle
	 * @param ga it is the GraphApp of the system
	 * @param computePath if it is true a path is computed, otherwise not.
	 */
	public VehicleApp(Vehicle v, BigInteger index, GraphApp ga, boolean computePath){
		vehicle = new Vehicle();
		graphApp = ga;
		
		//checks the starting node (and its port) and destination node really exists
		position = graphApp.getNode(v.getCurrentPosition());
		destination = graphApp.getNode(v.getDestination());
		if(position==null || destination==null) {
			logger.log(Level.WARNING, "The position or destination of vehicle " + v.getPlateNumber() + " doesn't exist");
			throw new BadRequestException();
		}

		//setting the plate of the model
		//PLATE
		vehicle.setPlateNumber(new String(v.getPlateNumber()));
		
		//try to add the vehicle in the system. If it cannot be added (constraint not respected) it will return false
		if(!position.checkConstraint()) {
			logger.log(Level.INFO, "The position of vehicle " + v.getPlateNumber() + " is full");
			throw new ForbiddenException("The vehicle cannot be added");
		}	
		//if the constraint are passed, I can enter
		position.addVehicle(this);
		
		//setting the fields of the model
		//INDEX
		vehicle.setID(index);
	
		//POSITIONS
		//Current
		vehicle.setCurrentPosition(new String(v.getCurrentPosition()));
		//Destination
		vehicle.setDestination(new String(v.getDestination()));
		
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
				logger.log(Level.INFO, "There is no path between " + position.getID()+ " and " + destination.getID());
				throw new ForbiddenException("There is no path acceptable between requested nodes.");
			}
		}
		else {
			path = null;
			vehicle.setState(State.PARKED);
		}
		
	}

	/**
	 * This function is able to update the position and/or destination of a vehicle. 
	 * 
	 * 
	 * I have four cases for this function
	 * 											   Dest
	 *                      |			==			|		!=				|
	 * 	CurrPos	        ==	|		nothing to do	|		new path		|
	 * 	                !=	|		update path		| 		new path		|
	 * 		
	 * Also, it is important to consider if a path already exist or not.
	 * If a path exist we must destroy it and compute a new path.
	 * 
	 * This method is synchronized to prevent to be executed more than once on each vehicle (anyway, it should be the normal behavior)
	 * 
	 * @param v the Vehicle model (only important fields are destination and currentPosition) 
	 */
	public synchronized void update(Vehicle v) {
		NodeApp npos = graphApp.getNode(v.getCurrentPosition());
		NodeApp ndes = graphApp.getNode(v.getDestination());
		if(npos==null || ndes == null) {
			logger.log(Level.WARNING, "The position or/and destination of vehicle " + v.getPlateNumber() + " doesn't exist");
			throw new BadRequestException();
		}
		
		vehicle.setLastUpdate(getXMLGregorianCalendarNow());
		
		if( ndes == destination && npos != position) {
			
			position.removeVehicle(this);
			position = npos;
			//I consider that a vehicle already there can fit in the node
			position.addVehicle(this);

			vehicle.setCurrentPosition(npos.getID());
			
			//if there is no path compute it
			if(path==null) {
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
					logger.log(Level.INFO, "There is no path between " + position.getID()+ " and " + destination.getID());
					throw new ForbiddenException("There is no path acceptable between requested nodes.");
				}
			}
			//otherwise update the path
			else {
				//Update path will update also the information on the nodes (future vehicles counter)
				//If the vehicle is no more following the path, a new one must be computed and the old one destroyed
				if( path.updatePath(position) ) {
					
					if(position == destination) {
						vehicle.setState(State.PARKED);
						vehicle.setPath(null);
						path = null;
					}
				}
				else {
					//Remove old path
					path.removePath();
					//compute new paths
					path = graphApp.getPath(position, destination);
					if(path != GraphApp.NO_PATH) {
						//a path exists and vehicle can go to destination
						setPath(path);
						vehicle.setState(State.TRANSIT);
					}
					else {
						//this can be an issue: if the vehicle goes into a position from which it cannot reach the destination, the behavior is unpredictable
						vehicle.setState(State.PARKED);
						vehicle.setPath(null);
						logger.log(Level.INFO, "There is no path between " + position.getID()+ " and " + destination.getID());
						throw new ForbiddenException("There is no path acceptable between requested nodes.");
					}
				}
				
			}
		
		}
		
		if(ndes != destination) {
			if(npos != position) {
				position.removeVehicle(this);
				position = npos;
				//I consider that a vehicle already there can fit in the node
				position.addVehicle(this);
				vehicle.setCurrentPosition(npos.getID());
			}
			destination = ndes;
			vehicle.setDestination(ndes.getID());
			//if path exist, remove it
			if(path!=null) {
				path.removePath();
			}
			//if destination == position
			if(destination==position) {
				vehicle.setState(State.PARKED);
				vehicle.setPath(null);
				path = null;
			}
			
			//compute new paths
			path = graphApp.getPath(position, destination);
			if(path != GraphApp.NO_PATH) {
				//a path exists and vehicle can go to destination
				setPath(path);
				vehicle.setState(State.TRANSIT);
			}
			else {
				//this can be an issue: if the vehicle goes into a position from which it cannot reach the destination, the behavior is unpredictable
				vehicle.setState(State.PARKED);
				vehicle.setPath(null);
				logger.log(Level.INFO, "There is no path between " + position.getID()+ " and " + destination.getID());
				throw new ForbiddenException("There is no path acceptable between requested nodes.");
			}				
		}
		return;	
	}
	
	/**
	 * This method update the position of the vehicle.
	 * If a vehicle has a path, it checks if the given position is following that. 
	 * If yes, the position is updated and the method returns.
	 * If not, a new path is computed from that position to the destination.
	 * 
	 * This method is synchronized to prevent to be executed more than once on each vehicle (anyway, it should be the normal behavior)
	 * 
	 * @param position the NodeApp correspondent to the position of the vehicle
	 * @return true if the path can be updated, false if a new path must be computed
	 */
	public synchronized boolean updatePosition(NodeApp position) {
		if(position == this.position) {
			return true;
		}
		
		this.position.removeVehicle(this);
		this.position = position;
		//I consider that a vehicle already there can fit in the node
		position.addVehicle(this);
		
		vehicle.setLastUpdate(getXMLGregorianCalendarNow());
		vehicle.setCurrentPosition(position.getID());
		
		//if path is not existent, return false
		if(path==null) {
			return false;
		}
		
		//Update path will update also the information on the nodes (future vehicles counter)
		//If the vehicle is no more following the path, a new one must be computed and the old one destroyed
		if( path.updatePath(position) ) {
			
			if(position == destination) {
				vehicle.setState(State.PARKED);
				vehicle.setPath(null);
				path = null;
			}
			return true;
		}
		else {
			//Remove old path
			path.removePath();
			//compute new paths
			path = graphApp.getPath(position, destination);
			if(path != GraphApp.NO_PATH) {
				//a path exists and vehicle can go to destination
				setPath(path);
			}
			else {
				//this can be an issue: if the vehicle goes into a position from which it cannot reach the destination, the behavior is unpredictable
				vehicle.setState(State.PARKED);
				vehicle.setPath(null);
				logger.log(Level.INFO, "There is no path between " + position.getID()+ " and " + destination.getID());
				throw new ForbiddenException("There is no path acceptable between requested nodes.");
			}
			return false;
		}
	}
	
	/**
	 *  The method clean the path (if exists) and then delete the vehicle from the Node.
	 */
	public synchronized void remove() {
		//Remove old path
		if(path != null ) {
			path.removePath();
		}
		position.removeVehicle(this);
		
	}
		
	public synchronized Vehicle getVehicle() {
		//convert the vehicle as the model needs
		return vehicle;
	}

	public NodeApp getPosition() {
		return position;
	}

	public NodeApp getDestination() {
		return destination;
	}

	public synchronized PathApp getPath() {
		return path;
	}

	private void setPath(PathApp path) {
		//this is called only by synchronized methods
		vehicle.setPath(path.getPath());
		this.path = path;
	}
	
	public BigInteger getID() {
		//this is an atomic operation, and also the ID is never modified
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
