package it.polito.dp2.vehicle.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Vehicle;

public class NodeApp {
	
	protected ArrayList<Edge> edges;
	protected LinkedList<Vehicle> vehicles;
	protected Node node;
	protected ArrayList<String> ports;
	protected static Logger logger = Logger.getLogger(VTService.class.getName());
	 
	public NodeApp(Node node) {
		this.node = node;
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

	public boolean addVehicle(Vehicle v) {
		if( checkConstraint() ) {
			logger.log(Level.INFO, "Added Vehicle " + v.getPlateNumber() + " to node " + node.getName());
			vehicles.add(v);
			return true;
		}
		logger.log(Level.INFO, "Unable to add vehicle " + v.getPlateNumber() + " to node " + node.getName());
		return false;
	}
	
	public void removeVehicle(Vehicle v) {
		vehicles.remove(v);
	}
	
	public List<Vehicle> getVehicles(){
		return vehicles;
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
