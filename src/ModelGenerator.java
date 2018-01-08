import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import it.polito.dp2.vehicle.model.*;

public class ModelGenerator {

	private static final int MIN_PORT_NUM = 3;
	private static final int BAS_PORT_NUM = 3;
	private static final int MAX_VEHICLES = 4;
	int parks, routes;
	int prob;
	int vehicles;
	ObjectFactory ofactory;
	Random random;
	
	/* 
	 * parkingAreas is the number of node of type parking wanted
	 * routes is the number of node of type route wanted
	 * p indicates the probability to have a node connected to another one.
	 * P must be a number between 0 and 100. 100 means connections with all, 0 means no connections.
	 * 
	 * The optional parameter seed is intended to allow the generation of the same model with the same seed 
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
	
	public ModelGenerator(int routes, int parkingAreas, int p, int vehicles, int seed) {
		this.parks = parkingAreas;
		this.routes = routes;
		this.prob = p;
		this.vehicles = vehicles;
		ofactory = new ObjectFactory();
		random = new Random(seed);
	}
	
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
	

	private void fillVehicles(Graph graph, List<Vehicle> vecs) {
		//Bugged, I'm not checking the max capacity of the node in which I am allocating the vehicle
		Vehicle v;
		
		for (int i = 0; i< vehicles; i++) {
			v = new Vehicle();
			int pos = random.nextInt(graph.getNode().size());
			NodeRef nr = new NodeRef();

			nr.setNode( graph.getNode().get(pos).getID());
			int portNum = random.nextInt(graph.getNode().get(pos).getPort().size());
			nr.setPort(  graph.getNode().get(pos).getPort().get(portNum));
			v.setCurrentPosition(nr);
			v.setDestination(graph.getNode().get(pos).getID());
			v.setID("V" + i);
			v.setState(State.PARKED);
			v.setPlateNumber("V" + i + "P" + pos + "IT");
			
			vecs.add(v);
		}
	}

	private void fillNodes(List<Node> nodes) {		
		Road r ;
		ParkingArea p;
		int portNum;
		for (int i = 1; i<=routes; i++) {
			r = new Road();
			r.setID("road"+ i);
			r.setName("Street " + i);
			r.setMaxVehicle(new BigInteger(MAX_VEHICLES, random).add(BigInteger.ONE));
			
			portNum = MIN_PORT_NUM + random.nextInt(BAS_PORT_NUM);
			
			List<String> ports = r.getPort();
			for(int j = 0; j < portNum; j++ ) {
				ports.add("Port"+j);
				if(random.nextInt(100) < 5) {
					r.setEndpoint( "Port"+j ); //5% of probability to be an end-point
				}
			}
			nodes.add(r);
		}
		
		for (int i = 1; i<=parks; i++) {
			p = new ParkingArea();
			p.setID("area"+ i);
			p.setName("Parking Area " + i);
			p.setMaxVehicle(new BigInteger(4, random));
			portNum = MIN_PORT_NUM + random.nextInt(BAS_PORT_NUM);
			List<String> ports = p.getPort();
			for(int j = 0; j < portNum; j++ ) {
				ports.add("Port"+j);
			}
			nodes.add(p);
		}
		
	}
	
	private void fillConnections(Graph graph) {
		/* For the moment this is bugged, a node can be isolated */
		List<Connection> conn = graph.getConnection();
		Connection ct;
		int pfr, pto;
		List<String> frPorts, toPorts ;
		NodeRef toref, frref;
		
		for(Node fr : graph.getNode() ) {
			frPorts = fr.getPort();
			for(Node to : graph.getNode()) {
				if(random.nextInt(100) < prob ) {
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
