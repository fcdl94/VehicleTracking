package it.polito.dp2.vehicle.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.Vehicle;

public class NodeApp {
	
	protected ArrayList<Edge> edges;
	protected LinkedList<Vehicle> vehicles;
	protected Node node;
	protected List<String> ports;
	 
	public NodeApp(Node node) {
		this.node = node;
		edges = new ArrayList<>();
		vehicles = new LinkedList<>();
		ports = node.getPort();
	}
	
	public void createEdge(NodeApp other, String tPort, String oPort) {
		if(this.containsPort(tPort)  && other.containsPort(oPort)) {
			Edge e = new Edge(this, other, tPort, oPort);
			edges.add(e);
		}
		System.err.println("Error - The model contains wrong references.\n\tFound creating the Edge from " + this.node.getName() + "to " + other.node.getName());
	}

	public void addVehicle(Vehicle v) {
		if( checkConstraint() )
			vehicles.add(v);
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
	private boolean containsPort(String port) {
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
}
