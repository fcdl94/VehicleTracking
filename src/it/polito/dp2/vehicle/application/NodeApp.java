package it.polito.dp2.vehicle.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;

public class NodeApp {
	
	protected ArrayList<Edge> edges;
	protected LinkedList<VehicleApp> vehicles;
	protected Node node;
	protected ArrayList<String> ports;
	protected static Logger logger = Logger.getLogger(VTService.class.getName());
	protected int futureVehicles; 
	/*
	 *	The variable futureVehicles is important to compute the vehicles that will pass from that node. 
	 *	It is limiting, but it is the only way to keep the system safe from overcrowded nodes.
	 *  The other way is to use only the current vehicles but if 3 vehicles ask for the same node and only 1 position is left, it is a problem because we cannot detect it
	*/
	
	public NodeApp(Node node) {
		this.node = node;
		this.futureVehicles = 0;
		edges = new ArrayList<>();
		vehicles = new LinkedList<>();
		ports = new ArrayList<String>(node.getPort());
		logger.log(Level.INFO, "Added Node " + node.getName());
	}
	
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

	public boolean addVehicle(VehicleApp v) {
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
	
	public void removeVehicle(VehicleApp v) {
		vehicles.remove(v);
	}
	
	public List<VehicleApp> getVehicles(){
		return vehicles;
	}
	
	public void incrementFutureVehicles() {
		futureVehicles++;
	}
	
	public void decrementFutureVehicles() {
		futureVehicles--;
	}
	
	/*
	 * Useful to check if the Node contains the port (only for checking correctness)
	 * 	
	 */
	protected boolean containsPort(String port) {
		if(ports.contains(port)) return true;
		else return false;
	}
	
	/*
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
