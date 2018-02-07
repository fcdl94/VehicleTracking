package it.polito.dp2.vehicle.application;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.PathNode;

public class PathApp {

	public static PathApp NO_PATH;
	
	Path path;
	LinkedList<NodeApp> nodes;
	LinkedList<Edge> edges;
	
	public PathApp(NodeApp from, NodeApp to, Map<NodeApp, Edge> parents) {
		nodes = new LinkedList<>();
		edges = new LinkedList<>();
		
		Edge e;
		//we start from the last node and we go up through parents until the from node
		NodeApp current;
		current = to;

		while( current != from ) {
			nodes.add(0, current);
			e = parents.get(current);
			//increment the number of vehicles that will pass in that Node
			current.incrementFutureVehicles();
			
			current = e.getFrom();
			edges.add(0, e);	
		}
		//I don't explicit add future vehicles of from Node because I suppose the vehicle is already there
		nodes.add(0, from);
		
		path = buildPath(edges);
		
	}

	public Path getPath() {
		return path;
	}
	
	//this function only makes the Path as the model asks
	private static Path buildPath(List<Edge> edges) {
		Path p = new Path();
		List<PathNode> pNodes = p.getNode();
		PathNode pNode;
	
		for(int i=0; i< edges.size(); i++) {
			
			pNode = new PathNode();
			//put it as the ith node
			pNode.setSequenceNum(i+1);
			
			//fill information for the path node
			pNode.setFrom(edges.get(i).getnrFrom());
			pNode.setTo(edges.get(i).getnrTo());
			pNodes.add(pNode); //add to the head, this is useful for numbering, the last added (starting point) will be the first in the list
		}

		return p;
	}
	
}
