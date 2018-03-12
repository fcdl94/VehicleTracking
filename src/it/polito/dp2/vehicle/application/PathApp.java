package it.polito.dp2.vehicle.application;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.PathNode;

/**
 * This class represents a Path and implements the methods to handle it.
 * The constant NO_PATH is useful to represent path that cannot be computed. 
 * 
 * 
 * @author Fabio Cermelli
 *
 */
public class PathApp {

	public static PathApp NO_PATH;
	
	Path path;
	LinkedList<NodeApp> nodes;
	LinkedList<Edge> edges;
	
	/**
	 * The constructor performs the conversion from a set of parents to a path.
	 * This is often used in the path computation as the last step.
	 * The parents represent a link between a NodeApp (the key) and the edge that reaches it,
	 * that is also the best to cross in order to go from node from to node to.
	 * 
	 * @param from the NodeApp representing the starting Node
	 * @param to the NodeApp representing the destination
	 * @param parents the map that links Node and edges that reaches it
	 */
	public PathApp(NodeApp from, NodeApp to, Map<NodeApp, Edge> parents) {
		nodes = new LinkedList<>();
		edges = new LinkedList<>();
		
		Edge e;
		//we start from the last node and we go up through parents until the from node
		NodeApp current;
		current = to;

		while( current != from ) {
			nodes.add(0, current); //add(0, NodeApp) means to add in the head
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

	/**
	 * This method is useful to update a path given a position inside it.
	 * If the position is not in the path, the method returns false and the path is unchanged.
	 * Otherwise, the path is updated according to the current position.
	 * 
	 * @param position the NodeApp corresponding to the current node
	 * @return false if the path cannot be updated, true otherwise
	 */
	public boolean updatePath(NodeApp position) {
		boolean ret;
	
		if(nodes.contains(position)) {
			ret = true;
			//delete the already passed nodes
			while(!nodes.get(0).equals(position)) {
				nodes.get(0).decrementFutureVehicles();
				nodes.remove(0);
				edges.remove(0);
				path.getNode().remove(0);
			}
			position.decrementFutureVehicles();
			//renumbering
			for(int i=0;i<edges.size(); i++) {
				path.getNode().get(i).setSequenceNum(i+1);
			}
			
		}
		else {
			ret = false;
		}	
		return ret;
	}
	
	/**
	 * This function has to be called when the path should be destroyed
	 */
	public void removePath() {
		//destroy the old path, cleaning all the nodes
		for(int i=0; i<nodes.size(); i++) {
			nodes.get(i).decrementFutureVehicles();
		}
		nodes.clear();
		edges.clear();
		path.getNode().clear();
	}
	
	
	/**
	 * this function only converts the PathApp to the model Path
	 */
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
