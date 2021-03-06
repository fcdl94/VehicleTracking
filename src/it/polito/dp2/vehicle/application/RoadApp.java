package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.logging.Level;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.Road;

/**
 * This class extend the NodeApp and represent a Road
 * @author Fabio Cermelli
 * @see {@linkplain NodeApp}
 *
 */
public class RoadApp extends NodeApp{

	private Road road;
	private BigInteger maxVehicle;
	
	public RoadApp(Node node) {
		super(node);
		if(node.getClass() == Road.class) {
			this.road = (Road) node;
			maxVehicle = road.getMaxVehicle();
			logger.log(Level.INFO, "Added Road " + node.getName());
		}
		else {
			logger.log(Level.SEVERE, "Road asked where the model isn't a road (" + node.getName() + ")");
		}
	}
	
	/**
	 * Useful to check if the port asked for entering the system is actually an end-point or not
	 */
	public boolean isEndpoint() {
		if(road.getEndpoint() != null) return true;
		else return false;
	}
	
	@Override
	protected boolean checkConstraint() {
		/*
		 * This function return true if the actual number of vehicle is lt the maximum value of vehicle acceptable
		 * E.g. with 9 vehicles inside and with 10 as maxVehicle, a vehicle can enter, so we allow returning true
		 */
		return vehicles.size() + futureVehicles < maxVehicle.intValue();
	}
	
}
