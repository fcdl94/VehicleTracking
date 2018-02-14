package it.polito.dp2.vehicle.client;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;
import it.polito.dp2.vehicle.model.Vehicles;

public class AdministratorClient {

		
	private Client client;
	
	public static void main(String[] args) {
		try{
			AdministratorClient admClient = new AdministratorClient();
			
			admClient.start();
			
		}catch(Exception ex ){
			System.err.println("Error during execution of remote operation");
			System.err.println(ex.getMessage());
		}
	}

	public AdministratorClient() {
		// build the client Jersey object 
		client = ClientBuilder.newClient();	
	}
	
	public void start() throws Exception {
		
		//get the list of the vehicles and print it.
		List<Vehicle> vehicles = getVehicles();
		
		for(Vehicle v : vehicles) {
			System.out.println(vehicleToString(v));
		}
			
	}
	
	List<Vehicle> getVehicles(){
		
		WebTarget target = client.target(getBaseURI());
		// perform a get request using mediaType=APPLICATION_XML
		// and convert the response into a normal string
		Vehicles resp = target.path("vehicles")
							   .request()
							   .accept(MediaType.APPLICATION_XML)
							   .get(Vehicles.class);
				
		return resp.getVehicle();
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
				sb.append("\n\t  " + p.getSequenceNum() + " From node " + p.getFrom().getNode() 
						+ " port " + p.getFrom().getPort() + " to node " + p.getTo().getNode() + " port " + p.getTo().getPort());
			}
		}	
		return sb.toString();
	}
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8080/vehicleTracking/webapi/").build();
	}
	
}
