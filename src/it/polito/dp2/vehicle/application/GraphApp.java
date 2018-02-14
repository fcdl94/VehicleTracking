package it.polito.dp2.vehicle.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.dp2.vehicle.model.Connection;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.ParkingArea;
import it.polito.dp2.vehicle.model.Road;

public class GraphApp {

	private ConcurrentSkipListMap<String, NodeApp> nodes;
	private Graph graph;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	public static PathApp NO_PATH = PathApp.NO_PATH;
	
	public GraphApp(Graph graph) {
		nodes = new ConcurrentSkipListMap<>();
		this.graph = graph;

		for (Node n :  this.graph.getNode() ) createNodeApp(n);
		
		for(Connection c : graph.getConnection()) createEdge(c);
		
	}
	
	public Graph getGraph() {	
		return graph;
	}	

	public NodeApp getNode(String node) {
		if(nodes.containsKey(node)) {
			return nodes.get(node);
		}
		return null;
	}
	
	public Set<VehicleApp> getVehicles(String node){
		if(nodes.containsKey(node)) {
			return nodes.get(node).getVehicles();
		}
		else {
			logger.log(Level.INFO, "The asked node " + node + " doesn't exist");
			return null;
		}
	}
		
	public PathApp getPath(NodeApp from,NodeApp to) {
		PathApp p;

		//evaluate all possible exception in arguments
		if( from==null || to==null ) return NO_PATH;
		
		//I am considering that: 
		// 1. The nodes in which I am respects the constraints, otherwise, I cannot be there
		// 2. The starting port is not considered in the Path evaluation because it is not relevant to the future positions
		// 3. The starting node and destination node are already checked to be valid (exist in the model). This can be assumed as always true since I take them always from GraphApp.
		
		//I return NO_PATH if the starting point is the same of the destination.
		if(from==to) return NO_PATH;
		
		p = BreadthFirst(from, to);
		
		return p;
	}
		
	private synchronized PathApp BreadthFirst(NodeApp from, NodeApp to) {
	//the synchronization is done at this level because otherwise the constraint on the Node could be violated.	
		
		
		List<NodeApp> queue = new LinkedList<>();
		HashSet<String> closed = new HashSet<>();
		HashSet<String> open = new HashSet<>(); //This list is useful only to check faster if a vehicle is already in "to visit" list
		Map<NodeApp, Edge> parents = new HashMap<>();
		
		//I take the starting node and put it into the "To visit" list, i.e. queue
		NodeApp parent;
		queue.add(from);
		
		//I loop until the "to visit" is empty or the solution is found (actual node to visit (parent) == destination (to) )
		while(!queue.isEmpty()) {
			//take and remove the first element, the high priority node to visit
			parent = queue.remove(0);
			//remove it from the opened and put it into the closed, make this here prevent considering edges in the same node, making infinite loops in the algorithms
			closed.add(parent.getID());
			
			if(parent == to) {
				//the solution has been found, clean, return it as new PathApp
				return new PathApp(from, to, parents);
			}
			//for all the edges reachable from the node visited
			for(Edge edg : parent.getEdges()) {
				if(!closed.contains(edg.getTo().getID())) {  //check if not already visited
					if(edg.getTo().checkConstraint()) { //check if it is crossable
						if(!open.contains(edg.getTo().getID())) { //check if not already listed
							// intelligence about the load balance must be put in previous if (otherwise, we will visit the same node until it is full)
							queue.add(edg.getTo());	//add to the "to visit" list the node
							open.add(edg.getTo().getID()); //add to open list
							parents.put(edg.getTo(), edg); //put in parents map the information that the node can be reached from the node we are visiting
						}
					}
				}
			}
			//we have concluded the visit on this node
		}
		return NO_PATH;
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
		if(c.getFrom() == null || c.getTo()==null) {
			logger.log(Level.SEVERE,"Connection have not full information");
			//should I throw an exception or only skip this connection
			//anyway, the model will be validated, this exception will be never raised if validation is done right
			return;
		}
		if(c.getFrom().getNode() == null || c.getFrom().getPort()==null || c.getTo().getNode()==null | c.getTo().getPort()==null) {
			logger.log(Level.SEVERE,"Connection have not full information");
			//should I throw an exception or only skip this connection
			//anyway, the model will be validated, this exception will be never raised if validation is done right
			return;
		}
		NodeApp nap = nodes.get(c.getFrom().getNode());
		NodeApp other = nodes.get(c.getTo().getNode());
		
		nap.createEdge(other, c.getFrom(), c.getTo());
		
	}	
}
