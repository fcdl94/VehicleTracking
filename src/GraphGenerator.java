import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBElement;

import vehicle.tr.ConnectionType;
import vehicle.tr.GraphType;
import vehicle.tr.ObjectFactory;
import vehicle.tr.ParkingAreaType;
import vehicle.tr.RoadType;
import vehicle.tr.NodeType;

public class GraphGenerator {

	int parks, routes;
	int prob;
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
	public GraphGenerator(int parkingAreas, int routes, int p) {
		this.parks = parkingAreas;
		this.routes = routes;
		this.prob = p;
		ofactory = new ObjectFactory();
		random = new Random();
	}
	
	public GraphGenerator(int parkingAreas, int routes, int p, int seed) {
		this.parks = parkingAreas;
		this.routes = routes;
		this.prob = p;
		ofactory = new ObjectFactory();
		random = new Random(seed);
	}
	
	public JAXBElement<GraphType> createRandomGraph(){
		GraphType graph = new GraphType();
		
		fillNodes(graph.getNode());
		
		fillConnections(graph.getConnection());	
		
		return ofactory.createGraph(graph);
	}
	

	private void fillNodes(List<NodeType> nodes) {		
		RoadType r ;
		ParkingAreaType p;
		for (int i = 1; i<=routes; i++) {
			r = new RoadType();
			r.setID("road"+ i);
			r.setName("Street " + i);
			r.setMaxVehicle(new BigInteger(4, random));
			r.setEndpoint( random.nextInt(100) < 20 ); //20% of probability to be an end-point
			nodes.add(r);
		}
		
		for (int i = 1; i<=parks; i++) {
			p = new ParkingAreaType();
			p.setID("area"+ i);
			p.setName("Parking Area " + i);
			p.setMaxVehicle(new BigInteger(4, random));
			nodes.add(p);
		}
		
	}
	
	private void fillConnections(List<ConnectionType> conn) {
		/* For the moment this is bugged, a node can be isolated */
		ConnectionType ct;
		for (int i = 1; i <= routes; i++) {
			for(int j = i; j <= routes; j++) {
				if (i != j ) {
					if(random.nextInt(100) < prob ) {
						ct = new ConnectionType();
						ct.setFrom("road"+i);
						ct.setTo("road"+j);
						conn.add(ct);
					}
				}
				
			}
			for(int j = 1; j <= parks; j++) {
				if (i != j ) {
					if(random.nextInt(100) < prob ) {
						ct = new ConnectionType();
						ct.setFrom("road"+i);
						ct.setTo("area"+j);
						conn.add(ct);
					}
				}
			}
			
		}
		
		for (int i = 1; i <= parks; i++) {
			for(int j = i; j <= routes; j++) {
				if (i != j ) {
					if(random.nextInt(100) < prob ) {
						ct = new ConnectionType();
						ct.setFrom("area"+i);
						ct.setTo("road"+j);
						conn.add(ct);
					}
				}
				
			}
			for(int j = 1; j <= parks; j++) {
				if (i != j ) {
					if(random.nextInt(100) < prob ) {
						ct = new ConnectionType();
						ct.setFrom("area"+i);
						ct.setTo("area"+j);
						conn.add(ct);
					}
				}
			}
			
		}
		
	}
}
