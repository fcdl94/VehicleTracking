package it.polito.dp2.vehicle.client;

public class SingleVehicle {

	public static void main(String[] args) {
		try{
			
			VehicleClient vehClient = new VehicleClient("road2", "area2", "CL001AA", "road3"); // I suppose to know the model
			vehClient.run();
			
		}catch(Exception ex ){
			System.err.println("Error during execution of remote operation");
			System.err.println(ex.getMessage());
		}
	}
	
	
}
