package it.polito.dp2.vehicle.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;

/**
 * This super-class represents a general node and implements the basic methods to perform operation on the nodes.
 * 
 * @see {@link GraphApp}, {@link Node}, {@link RoadApp}, {@link ParkingAreaApp}
 * @author Fabio Cermelli
 *
 */
public class NodeApp {
	
	protected List<Edge> edges;
	protected Set<VehicleApp> vehicles;
	protected Node node;
	protected List<String> ports;
	protected static Logger logger = Logger.getLogger(VTService.class.getName());
	protected int futureVehicles; 
	/*
	 *	The variable futureVehicles is important to compute the vehicles that will pass from that node. 
	 *	It is limiting, but it is the only way to keep the system safe from overcrowded nodes.
	 *  The other way is to use only the current vehicles but if 3 vehicles ask for the same node and only 1 position is left, it is a problem because we cannot detect it
	*/
	
	/**
	 * The constructor takes the node and initialize all the parameters.
	 * It is done always is initialization step
	 * 
	 * @param node the model of the node to represent
	 */
	public NodeApp(Node node) {
		this.node = node;
		this.futureVehicles = 0;
		edges = Collections.synchronizedList(new ArrayList<>());
		vehicles = new CopyOnWriteArraySet<>();
		ports = Collections.synchronizedList(new ArrayList<String>(node.getPort()));
		logger.log(Level.INFO, "Added Node " + node.getName());
	}
	
	/**
	 * This method creates and store an edges from this node to another node.
	 * 
	 * @see {@linkplain Edge}, {@linkplain NodeRef}
	 * 
	 * @param other the NodeApp that is reached with the edge
	 * @param from the NodeRef from which the edge starts
	 * @param to the NodeRef that is reached
	 */
	public void createEdge(NodeApp other, NodeRef from, NodeRef to) {
		if(this.containsPort(from.getPort())  && other.containsPort(to.getPort())) {
			Edge e = new Edge(this, other, from, to);
			edges.add(e);
			logger.log(Level.INFO, "Added Edge from " + this.node.getName() + "to " + other.node.getName());
		}
		else {
			logger.log(Level.SEVERE, "The model contains wrong references.\n\tFound creating the Edge from " + this.node.getName() + "to " + other.node.getName());
			//should I throw an exception?
		}
	}

	/**
	 * The methods adds a vehicle in the node.
	 * It is important to check the constraint before adding it but the vehicle is added also if the constraint are not satisfied.
	 * This is done to overcome some issue when a vehicle is in a node that exceed the maximum capacity (can happen in real application).
	 * If you want to be sure that the constraint are not broken, use checkConstraint before calling this.
	 * 
	 * It is important to remark that the method is synchronized to prevent race conditions.
	 * 
	 * @param v the VehicleApp to be added in the node
	 * @return true if constraint are respected, false otherwise.
	 */
	public synchronized boolean addVehicle(VehicleApp v) {
		boolean ret;
		if( checkConstraint() ) {
			logger.log(Level.INFO, "Added Vehicle " + v.getPlateNumber() + " to node " + node.getName());
			ret = true;
		}
		else {
			logger.log(Level.INFO, "Adding a vehicle " + v.getPlateNumber() + " even if the node "+ node.getName() + " is full");
			ret = false;
		}
		//I add the vehicle anyway, but I'll signal it with the return value
		vehicles.add(v);
		return ret;
	}
	
	/**
	 * When a vehicle exits the node, it can be remove with this method
	 * @param v the VehicleApp of the exiting vehicle
	 */
	public void removeVehicle(VehicleApp v) {
		vehicles.remove(v);
	}
	
	public Set<VehicleApp> getVehicles(){
		return vehicles;
	}
	
	/**
	 * This implement the reservation mechanism.
	 * When a node chooses to cross a node, it reserves it using this method and preventing to broke the constraints
	 */
	public synchronized void incrementFutureVehicles() {
		futureVehicles++;
	}
	
	/**
	 * This implement the reservation mechanism.
	 * When a node does not want anymore to cross a node, it can remove the reservation using this method.
	 */
	public synchronized void decrementFutureVehicles() {
		futureVehicles--;
	}
	
	/**
	 * Useful to check if the Node contains the port (only for checking correctness)
	 * 	
	 */
	protected boolean containsPort(String port) {
		if(ports.contains(port)) return true;
		else return false;
	}
	
	/**
	 * Returns true if the constraint are respected
	 * Given the general Node Class, we have not special constraint, so I assumes that a new vehicles can always added
	 * 
	 */
	protected boolean checkConstraint() {
		return true;
	}

	protected List<Edge> getEdges() {
		return edges;
	}

	protected String getID() {
		return node.getID();
	}

}
