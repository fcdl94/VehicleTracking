package it.polito.dp2.vehicle.application;

import java.math.BigInteger;
import java.util.logging.Level;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.ParkingArea;

/**
 * This class extend the NodeApp and represent a ParkingArea
 * @author Fabio Cermelli
 * @see {@linkplain NodeApp}
 *
 */
public class ParkingAreaApp extends NodeApp{

	private ParkingArea parking;
	private BigInteger maxVehicle;
	
	public ParkingAreaApp(Node node) {
		super(node);
		if(node.getClass() == ParkingArea.class) {
			this.parking = (ParkingArea) node;
			maxVehicle = parking.getMaxVehicle();
			logger.log(Level.INFO, "Added PArea" + node.getName());

		}
		else {
			logger.log(Level.SEVERE, "ParkingArea asked where the model isn't a parkingArea (" + node.getName() + ")");
			//should I throw an exception?
		}
	}
		
	@Override
	protected boolean checkConstraint() {
		/*
		 * This function return true if the actual number of vehicle is lt the maximum value of vehicle acceptable
		 * E.g. with 9 vehicles inside and with 10 as maxVehicle, a vehicle can enter, so we allow returning true
		 */
		if(futureVehicles < 0 )
			logger.log(Level.SEVERE, "future vehicles" + futureVehicles + " size" + vehicles.size());
		return vehicles.size() + futureVehicles < maxVehicle.intValue();
	}
	
}
