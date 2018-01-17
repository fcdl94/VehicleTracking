package it.polito.dp2.vehicle.application;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;

import it.polito.dp2.vehicle.model.Connection;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.ParkingArea;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.Road;
import it.polito.dp2.vehicle.model.Vehicle;

public class GraphApp {

	private ConcurrentSkipListMap<String, NodeApp> nodes;
	private Graph graph;
	private static Logger logger = Logger.getLogger(VTService.class.getName());
	
	public GraphApp(Graph graph) {
		nodes = new ConcurrentSkipListMap<>();
		this.graph = graph;

		for (Node n :  this.graph.getNode() ) createNodeApp(n);
		
		for(Connection c : graph.getConnection()) createEdge(c);
		
	}

	
	public List<Vehicle> getVehicles(String node){
		if(nodes.containsKey(node)) {
			return nodes.get(node).getVehicles();
		}
		else {
			logger.log(Level.WARNING, "The asked node " + node + " doesn't exist");
			//TODO Should be better raise an exception
			return null;
		}
	}
	
	public boolean addVehicle(Vehicle v) {
		NodeApp nap;
		if( v.getCurrentPosition() != null && nodes.containsKey(v.getCurrentPosition().getNode()) ) {
			 nap = nodes.get(v.getCurrentPosition().getNode());
			 return nap.addVehicle(v);
		}
		else {
			//TODO should be better raise an exception
			logger.log(Level.WARNING, "Vehicle " + v.getPlateNumber() + "have no current position");
			return false;
		}
	}
	
	
	
	public Path getPath(NodeRef position, String destination) {
		Path p = new Path();
		NodeApp from;
		NodeApp to;
		//evaluate all possible exception in arguments
		if( position==null || destination==null ) throw new BadRequestException();
		
		if(!nodes.containsKey(position.getNode()) || !nodes.containsKey(destination) )
			throw new BadRequestException();
		from = nodes.get(position.getNode());
		to = nodes.get(destination);
		
		if(!from.containsPort(position.getPort())) throw new BadRequestException();
		
		//Now we know all data are correct, proceed with path evaluation
		//TODO Breath First algorithm
		
		
		
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
		
		nap.createEdge(other, c.getFrom().getPort(), c.getTo().getPort());
		
	}

	
}
