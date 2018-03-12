package it.polito.dp2.vehicle.client;

import java.net.URI;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


import it.polito.dp2.vehicle.model.ObjectFactory;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;

public class VehicleClient implements Runnable {

	//private Graph graph;
	private Vehicle vehicle;
	private String position;
	private String destination;
	private String plate;
	private String endPoint;
	private Random random;
	
	private ObjectFactory obf = new ObjectFactory();
	
	private Client client;
	
	/**
	 * The constructor of a client
	 */
	public VehicleClient(String position,String destination, String plate, String endPoint) {
		//this client is dependent on the model and I assume it know the graph 
		this.position =position;
		this.destination = destination;
		this.plate = plate;
		this.endPoint = endPoint;
		
		this.random = new Random();
		
		
		
        //Logger logger = Logger.getLogger(getClass().getName());

        //Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

        client = ClientBuilder.newBuilder()
        //        .register(feature)
                .build();
		
	}
	
	/**
	 * The method implements the behavior of a standard client. 
	 * It starts from position and goes to destination.
	 * While there it asks to go to endPoint, the exit point.
	 * 
	 */
	public void run() {
		
		try {
			
		createVehicle();

		Thread.sleep(1000 + random.nextInt(2000));
		
		String nextPos = position;
		int indexPath = 0;
		
		while(!nextPos.equals(destination)) {
			nextPos = vehicle.getPath().getNode().get(indexPath).getTo().getNode();
			//POST the new position
			if(postPosition(nextPos)) {
				indexPath++;
				System.out.println(vehicle.getPlateNumber() + ": goes from "+ position + " to " + nextPos);
				position = nextPos;
				
			}
			else indexPath = 0;
			
			Thread.sleep(random.nextInt(2000));
		}
		
		Thread.sleep(1000 + random.nextInt(2000));
		//I must be sure that is a end-point
		destination = endPoint;
		putVehicle();
		
		indexPath = 0;
		
		while(!nextPos.equals(destination)) {
			nextPos = vehicle.getPath().getNode().get(indexPath).getTo().getNode();
			//POST the new position
			if(postPosition(nextPos)) {
				indexPath++;
				position = nextPos;
			}
			else indexPath = 0;
		
			Thread.sleep(random.nextInt(2000));
		
		}
		
		Thread.sleep(1000 + random.nextInt(2000));
		deleteVehicle();
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		catch (ConnectionException ce) {
			System.err.println(ce.getMessage());
		}
		
	}
	/**
	 * It perform the HTTP POST operation to create a vehicle
	 */
	private void createVehicle() throws ConnectionException{
	
		Vehicle v = new Vehicle();
		v.setCurrentPosition(position);
		v.setDestination(destination);
		v.setPlateNumber(plate);
		
		Response resp = null;
		
		WebTarget target = client.target(getBaseURI());
		// perform a get request using mediaType=APPLICATION_XML
		// and convert the response into a normal string
		 resp = target.path("vehicles")
						   .request()
						   .accept(MediaType.APPLICATION_XML)
						   .post(Entity.entity(obf.createVehicle(v), MediaType.APPLICATION_XML));
		
		if(resp.getStatus() == 200) {
			if(resp.getEntity() != null) {
				vehicle =  ( resp.readEntity(Vehicle.class));
				System.out.println(vehicleToString(vehicle));
			}	
		}
		else {
			System.out.println("STATUS:" + resp.getStatus());
			throw new ConnectionException("" + resp.getStatus());
		}
	}
	
	/**
	 * It performs the HTTP POST operation to update the vehicle position
	 * 
	 */
	private boolean postPosition(String nextPos) throws ConnectionException {
		WebTarget target = client.target(vehicle.getSelf().getHref());
		
		
		Response resp = null;
		resp = target.request()
			   .accept(MediaType.APPLICATION_XML)
			   .post(Entity.entity(nextPos, MediaType.TEXT_PLAIN));
		
		if(resp.getStatus() == 200) {
			if(resp.getEntity() != null) {
				vehicle =  ( resp.readEntity(Vehicle.class));
				System.out.println("I was out of path, those are the new info\n");
				System.out.println(vehicleToString(vehicle));
			}
			return false;
		}
		else if (resp.getStatus() == 204) {
			return true;
		}
		else {
			System.out.println("STATUS:" + resp.getStatus());
			vehicle = null;
			throw new ConnectionException("" + resp.getStatus());
		}
	
	}
	
	/**
	 * It performs the HTTP PUT operation to update the vehicle information
	 * 
	 */
	private void putVehicle() throws ConnectionException {
		WebTarget target = client.target(vehicle.getSelf().getHref());
		
		vehicle.setCurrentPosition(position);
		vehicle.setDestination(destination);
		
		Response resp = null;
		
		resp = target.request()
			   .accept(MediaType.APPLICATION_XML)
			   .put(Entity.entity(obf.createVehicle(vehicle), MediaType.APPLICATION_XML));
		 
		if(resp.getStatus() == 200) {
			if(resp.getEntity() != null) {
				vehicle =  ( resp.readEntity(Vehicle.class));
				System.out.println(vehicleToString(vehicle));
			}	
		}
		else {
			System.out.println("STATUS:" + resp.getStatus());
			throw new ConnectionException("" + resp.getStatus());
		}
		
	}
	
	
	/**
	 * It performs the HTTP DELETE operation to remove the vehicle from the system
	 * 
	 */
	private void deleteVehicle() throws ConnectionException{
		
		WebTarget target = client.target(vehicle.getSelf().getHref());
		
		Response resp = null;
		
		resp = target.request()
				   .delete();
		
		if(resp.getStatus() == 204) {
			System.out.println("Vehicle exited the system");
			return;
		}
		else {
			System.out.println("STATUS:" + resp.getStatus());
			throw new ConnectionException("" + resp.getStatus());
		}
	}
	
	private String vehicleToString(Vehicle v) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Vehicle ID:"+ v.getID());
		sb.append("\n\t" + "Plate: " + v.getPlateNumber());
		sb.append("\n\t" + "Position: " + v.getCurrentPosition());
		sb.append("\n\t" + "Destination: " + v.getDestination());
		sb.append("\n\t" + "State: " + v.getState());
	
		Path path = v.getPath();
		if(path != null) {
			sb.append("\n\t" + "Path: \n");
			for(PathNode p : path.getNode()) { 
				sb.append("\n\t  " + p.getSequenceNum() + " From node " + "\"" + p.getFrom().getNode() + "\"" 
						+ " port " + "\"" + p.getFrom().getPort() + "\"" + " to node \"" + p.getTo().getNode() + "\" port \"" + p.getTo().getPort() +"\"");
			}
		}	
		return sb.toString();
	}
	
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8080/vehicleTracking/webapi/").build();
	}
}
