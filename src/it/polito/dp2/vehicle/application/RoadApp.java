package it.polito.dp2.vehicle.application;

import java.math.BigInteger;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.Road;

public class RoadApp extends NodeApp{

	private Road road;
	private BigInteger maxVehicle;
	
	public RoadApp(Node node) {
		super(node);
		if(node.getClass() == Road.class) {
			this.road = (Road) node;
			maxVehicle = road.getMaxVehicle();
		}
		else {
			System.err.println("Error, road asked where the model isn't a road.");
		}
	}
	
	/*
	 * Useful to check if the port asked for entering the system is actually an endpoint or not
	 */
	public boolean isEndpoint(String port) {
		if(road.getEndpoint().equals(port)) return true;
		else return false;
	}
	
	@Override
	protected boolean checkConstraint() {
		/*
		 * This function return true if the actual number of vehicle is lt the maximum value of vehicle acceptable
		 * E.g. with 9 vehicles inside and with 10 as maxVehicle, a vehicle can enter, so we allow returning true
		 */
		return vehicles.size() < maxVehicle.intValue();
	}
	
}
