package it.polito.dp2.vehicle.client;

public class MultiVehicle {

	public static void main(String[] args) {
		try{
			
			VehicleClient vehClient1 = new VehicleClient("road1", "area10", "CL001AA", "road3"); // I suppose to know the model
			VehicleClient vehClient2 = new VehicleClient("road2", "area5", "CL002AA", "road3"); // I suppose to know the model
			VehicleClient vehClient3 = new VehicleClient("road3", "area2", "CL003AA", "road3"); // I suppose to know the model
			VehicleClient vehClient4 = new VehicleClient("road4", "area1", "CL004AA", "road3"); // I suppose to know the model
			VehicleClient vehClient5 = new VehicleClient("road5", "area2", "CL005AA", "road3"); // I suppose to know the model
			VehicleClient vehClient6 = new VehicleClient("road4", "area2", "CL006AA", "road3"); // I suppose to know the model
			VehicleClient vehClient7 = new VehicleClient("road3", "area1", "CL007AA", "road3"); // I suppose to know the model

			
			Thread t1 = new Thread(vehClient1, "VEH1") ;
			Thread t2 = new Thread(vehClient2, "VEH2") ;
			Thread t3 = new Thread(vehClient3, "VEH3") ;
			Thread t4 = new Thread(vehClient4, "VEH4") ;
			Thread t5 = new Thread(vehClient5, "VEH5") ;
			Thread t6 = new Thread(vehClient6, "VEH6") ;
			Thread t7 = new Thread(vehClient7, "VEH6") ;
			
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			t6.start();
			t7.start();
			
			
		}catch(Exception ex ){
			System.err.println("Error during execution of remote operation");
			System.err.println(ex.getMessage());
		}
	}
	
	
}

