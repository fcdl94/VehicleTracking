package it.polito.dp2.vehicle.application;

import java.util.HashMap;
import java.util.List;

import it.polito.dp2.vehicle.model.Connection;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.ParkingArea;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.Road;
import it.polito.dp2.vehicle.model.Vehicle;

public class GraphApp {

	private HashMap<String, NodeApp> nodes;
	private Graph graph;
	
	public GraphApp(Graph graph) {
		nodes = new HashMap<>();
		this.graph = graph;

		for (Node n :  this.graph.getNode() ) createNodeApp(n);
		
		for(Connection c : graph.getConnection()) createEdge(c);
		
	}

	public boolean containsNode(String node, String port) {
		if(nodes.containsKey(node)) {
			if(nodes.get(node).ports.contains(port)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsNode(String node) {
		if(nodes.containsKey(node)) {
			return true;
		}
		return false;
	}
	
	public List<Vehicle> getVehicles(String node){
		if(nodes.containsKey(node)) {
			return nodes.get(node).getVehicles();
		}
		else {
			if(System.getenv("DEBUG") != null) {
				System.out.println("ERROR - The asked node " + node + " doesn't exist");
			}
			//Should be better raise an exception
			return null;
		}
	}
	
	public boolean addVehicle(Vehicle v) {
		NodeApp nap = nodes.get(v.getCurrentPosition().getNode());
		return nap.addVehicle(v);
	}
	
	
	
	public Path getPath(NodeRef position, String destination) {
		Path p = new Path();
		//To do
		return p;
	}
		
	
	private void createNodeApp(Node n) {
		if(n.getClass().equals(Road.class)) {
			RoadApp rap = new RoadApp(n);
			nodes.put(n.getID(), rap);
		}
		else if(n.getClass().equals(ParkingArea.class)) {
			ParkingAreaApp rap = new ParkingAreaApp(n);
			nodes.put(n.getID(), rap);
		}
		else {
			NodeApp nap = new NodeApp(n);
			nodes.put(n.getID(), nap);
		}
	}
	
	private void createEdge(Connection c) {
		NodeApp nap = nodes.get(c.getFrom().getNode());
		NodeApp other = nodes.get(c.getTo().getNode());
		
		nap.createEdge(other, c.getFrom().getPort(), c.getTo().getPort());
		
	}

	
}
