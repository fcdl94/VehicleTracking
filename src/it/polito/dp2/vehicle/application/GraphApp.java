package it.polito.dp2.vehicle.application;

import java.util.HashMap;

import it.polito.dp2.vehicle.model.Connection;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Path;

public class GraphApp {

	private HashMap<String, NodeApp> nodes;
	private Graph graph;
	
	public GraphApp(Graph graph) {
		nodes = new HashMap<>();
		this.graph = graph;
		//filling nodes from graph - must be the specific type and not the general!!!!!!
		//TO IMPROVE
		for (Node n :  graph.getNode() ) createNodeApp(n);
		
		for(Connection c : graph.getConnection()) createEdge(c);
		
	}
	
	public Path getPath(NodeRef position, String destination) {
		Path p = new Path();
		//To do
		
		
		return p;
	}
	
	
	private void createNodeApp(Node n) {
		NodeApp nap = new NodeApp(n);
		nodes.put(n.getID(), nap);
	}
	
	private void createEdge(Connection c) {
		NodeApp nap = nodes.get(c.getFrom().getNode());
		NodeApp other = nodes.get(c.getTo().getNode());
		
		nap.createEdge(other, c.getFrom().getPort(), c.getTo().getPort());
		
	}

	
}
