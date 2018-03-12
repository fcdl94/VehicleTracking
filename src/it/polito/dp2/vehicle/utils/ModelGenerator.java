package it.polito.dp2.vehicle.utils;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import it.polito.dp2.vehicle.model.*;

/**
 * This class implements a random Model Generator. 
 *  
 * 
 * @author Fabio Cermelli
 *
 */
public class ModelGenerator {

	private static final int MIN_PORT_NUM = 3;
	private static final int BAS_PORT_NUM = 2;
	private static final int MAX_VEHICLES = 25;
	int parks, routes;
	int prob;
	int vehicles;
	ObjectFactory ofactory;
	Random random;
	
	/**
	 * 
	 * 
	 * @param parkingAreas is the number of node of type parking wanted
	 * @param routes is the number of node of type route wanted
	 * @param p indicates the probability to have a node connected to another one; must be a number between 0 and 100. 100 means connections with all, 0 means no connections.
	 * 
	 */
	public ModelGenerator(int routes, int parkingAreas, int vehicles, int p) {
		this.parks = parkingAreas;
		this.routes = routes;
		this.prob = p;
		this.vehicles = vehicles;
		ofactory = new ObjectFactory();
		random = new Random();
	}
	
	/**
	 * @param parkingAreas is the number of node of type parking wanted
	 * @param routes is the number of node of type route wanted
	 * @param p indicates the probability to have a node connected to another one; must be a number between 0 and 100. 100 means connections with all, 0 means no connections.
	 * 
	 *  
	 * @param seed is intended to allow the generation of the same model with the same seed 
	 * 
	 */
	public ModelGenerator(int routes, int parkingAreas, int p, int vehicles, int seed) {
		this.parks = parkingAreas;
		this.routes = routes;
		this.prob = p;
		this.vehicles = vehicles;
		ofactory = new ObjectFactory();
		random = new Random(seed);
	}
	
	/**
	 * This method returns a random model with N nodes where N = parkingAreas + routes;
	 * The model generated has V vehicles where V = vehicles
	 * 
	 */
	public Model createRandomModel(){
		Model model = new Model();
		
		Graph graph = new Graph();
		fillNodes(graph.getNode());
		fillConnections(graph);	
		
		Vehicles vehicles = new Vehicles();
		fillVehicles(graph, vehicles.getVehicle());
		
		model.setGraph(graph);
		model.setVehicles(vehicles);
		
		return model;
	}
	
	/**
	 * This method generates the vehicle and adds it to the model
	 * 
	 * @param graph the model where the nodes can be retrieved
	 * @param vecs the list of vehicles that has to be filled
	 */
	private void fillVehicles(Graph graph, List<Vehicle> vecs) {
		//WARNING I'm not checking the max capacity of the node in which I am allocating the vehicle
		//THUS it can be generated nodes with too much vehicles, exceeding the constraint
		Vehicle v;
		
		for (int i = 0; i< vehicles; i++) {
			v = new Vehicle();
			int pos = random.nextInt(graph.getNode().size());
			
			v.setCurrentPosition(graph.getNode().get(pos).getID());
			v.setDestination(graph.getNode().get(pos).getID());
			v.setID(BigInteger.valueOf(i));
			v.setState(State.PARKED);
			v.setPlateNumber("V" + i + "P" + pos + "IT");
			
			vecs.add(v);
		}
	}

	/**
	 * This method creates the Node of the graph.
	 * @param nodes the list to be filled
	 */
	private void fillNodes(List<Node> nodes) {		
		Road r ;
		ParkingArea p;
		int portNum;
		long maxVeh;
		for (int i = 1; i<=routes; i++) {
			r = new Road();
			r.setID("road"+ i);
			r.setName("Street " + i);
			maxVeh = random.nextInt(MAX_VEHICLES-5) + 5;
			r.setMaxVehicle(BigInteger.valueOf(maxVeh));
			
			portNum = MIN_PORT_NUM + random.nextInt(BAS_PORT_NUM);
			
			List<String> ports = r.getPort();
			for(int j = 0; j < portNum; j++ ) {
				ports.add("port"+j);
				if(random.nextInt(100) < 10) {
					r.setEndpoint( "port"+j ); //20% of probability to be an end-point
				}
			}
			nodes.add(r);
		}
		
		for (int i = 1; i<=parks; i++) {
			p = new ParkingArea();
			p.setID("area"+ i);
			p.setName("Parking Area " + i);
			maxVeh = random.nextInt(MAX_VEHICLES-5) + 5;
			p.setMaxVehicle(BigInteger.valueOf(maxVeh));
			
			portNum = MIN_PORT_NUM + random.nextInt(BAS_PORT_NUM);
			
			List<String> ports = p.getPort();
			for(int j = 0; j < portNum; j++ ) {
				ports.add("port"+j);
			}
			nodes.add(p);
		}
		
	}
	/**
	 * Given the nodes, it generates the connections between them
	 * 
	 * @param graph the model where the connection must be added
	 */
	private void fillConnections(Graph graph) {
		//WARNING It can be generated an isolated node, it means a node without incoming link.
		List<Connection> conn = graph.getConnection();
		Connection ct;
		int pfr, pto;
		List<String> frPorts, toPorts ;
		NodeRef toref, frref;
		
		for(Node fr : graph.getNode() ) {
			frPorts = fr.getPort();
			for(Node to : graph.getNode()) {
				if(random.nextInt(100) < prob && !to.equals(fr)) {
					toPorts = to.getPort();
					ct = new Connection();
					toref = new NodeRef();
					frref = new NodeRef();
					pfr = random.nextInt(frPorts.size());
					pto = random.nextInt(toPorts.size());
					
					frref.setNode(fr.getID());
					frref.setPort(frPorts.get(pfr));
					toref.setNode(to.getID());
					toref.setPort(toPorts.get(pto));
					
					ct.setFrom(frref);
					ct.setTo(toref);
					
					conn.add(ct);
				}
			}
		}
		
		
		
	}
}
