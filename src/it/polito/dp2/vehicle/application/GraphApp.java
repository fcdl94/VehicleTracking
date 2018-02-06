package it.polito.dp2.vehicle.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;

import it.polito.dp2.vehicle.model.Connection;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.ParkingArea;
import it.polito.dp2.vehicle.model.Road;

public class GraphApp {

	private ConcurrentSkipListMap<String, NodeApp> nodes;
	private Graph graph;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	public static PathApp NO_PATH = new PathApp();
	
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
	
	public List<VehicleApp> getVehicles(String node){
		if(nodes.containsKey(node)) {
			return nodes.get(node).getVehicles();
		}
		else {
			logger.log(Level.WARNING, "The asked node " + node + " doesn't exist");
			//TODO Should be better raise an exception
			return null;
		}
	}
	
	public boolean addVehicle(VehicleApp v) {
		NodeApp nap;
		if( v.getPosition() != null && nodes.containsKey(v.getPosition().getID()) ) {
			 nap = v.getPosition();
			 return nap.addVehicle(v);
		}
		else {
			//TODO should be better raise an exception
			logger.log(Level.WARNING, "Vehicle " + v.getPlateNumber() + "have no current position");
			return false;
		}
	}	
	
	public PathApp getPath(NodeRef position, String destination) {
		PathApp p;
		NodeApp from;
		NodeApp to;
		//evaluate all possible exception in arguments
		if( position==null || destination==null ) throw new BadRequestException();
		if(position.getNode() == null || position.getPort() == null) throw new BadRequestException();
		
		if(!nodes.containsKey(position.getNode()) || !nodes.containsKey(destination) )
			throw new BadRequestException();
		
		from = nodes.get(position.getNode());
		to = nodes.get(destination);
		
		if(!from.containsPort(position.getPort())) throw new BadRequestException();
		
		if(from==to) return NO_PATH;
		if(!from.checkConstraint()) return NO_PATH;
		//Now we know all data are correct, proceed with path evaluation
		
		p = BreadthFirst(from, to);
		
		return p;
	}
		
	private synchronized PathApp BreadthFirst(NodeApp from, NodeApp to) {
		
		List<NodeApp> queue = new LinkedList<>();
		HashSet<String> closed = new HashSet<>();
		HashSet<String> open = new HashSet<>(); //This list is useful only to check faster if a vehicle is already in "to visit" list
		Map<NodeApp, Edge> parents = new HashMap<>();
		
		//I take the starting node and put it into the "To visit" list, i.e. queue
		NodeApp parent;
		queue.add(from);
		
		//I loop untile the "to visit" is empty or the solution is found (actual node to visit (parent) == destination (to) )
		while(!queue.isEmpty()) {
			//take and remove the first element, the high priority node to visit
			parent = queue.remove(0);
			//remove it from the opened and put it into the closed, make this here prevent considering edges in the same node, making infinite loops in the algorithms
			closed.add(parent.getID());
			if(parent == to) {
				//the solution has been found, clean, format and return it
				return buildSolution(from, to, parents);
			}
			//for all the edges reachable from the node visited
			for(Edge edg : parent.getEdges()) {
				if(edg.getTo().checkConstraint()) { //check if it is crossable
					if(!closed.contains(edg.getTo().getID())) { //check if not already visited
						if(!open.contains(edg.getTo().getID())) { //check if not already listed
							//TODO intelligence about the load balance must be put in previous if (otherwise, we will visit the same node until it is full)
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
	
	private PathApp buildSolution(NodeApp from, NodeApp to,  Map<NodeApp, Edge> parents) {
		PathApp p = new PathApp();
		/*List<PathNode> pNodes = p.getNode();
		PathNode pNode;
		Edge e;
		//we start from the last node and we go up through parents until the from node
		NodeApp current = to;
		while(current != from) {
			//TODO add information that is a vehicle that will pass in the node
			pNode = new PathNode();
			//take the edges that binds parent with current node
			e = parents.get(current);
			//fill information for the path node
			pNode.setFrom(e.getnrFrom());
			pNode.setTo(e.getnrTo());
			pNodes.add(0, pNode); //add to the head, this is useful for numbering, the last added (starting point) will be the first in the list
			e.getTo().incrementFutureVehicles(); //increment the counter of the vehicles that will pass on that node
			current = e.getFrom();
		}
		for(int i = 0; i < pNodes.size(); i++) {
			pNodes.get(i).setSequenceNum(i);
		}
		from.incrementFutureVehicles();
		*/
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
