package it.polito.dp2.vehicle.utils;
import java.io.File;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.Vehicle;

public class ReadModel {

	private static String filename = "xml/xml-gen.xml";
	
	public static void main(String[] args) throws Throwable {
		JAXBContext jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
		Unmarshaller um = jc.createUnmarshaller();
		
		 Model model = (Model) um.unmarshal( new File( filename ) );
		 
		 System.out.println("Marhsalling done!");
		 
		 VTService vtservice = VTService.getVTService();
		 
		 System.out.println("Get istance of VTService");
		 
		 vtservice.setModel(model);
		 
		 System.out.println("Loaded model in VTService");
		
		 ReadModel rd1 = new ReadModel();
		 ReadModel rd2 = new ReadModel();
		 
		 rd1.run1("VN1P2P0IT");
		 rd2.run1("VN2P2P0IT");
		 rd1.run2("VN3P3P1IT");
		 rd2.run2("VN4P3P1IT");
		 
		 rd1.finalize();
		 rd2.finalize();
		 
		 
		 
		 for(Vehicle v : vtservice.getVehiclesFromNode("road3")) {
			 System.out.println("The vehicle " + v.getPlateNumber() + " have destination " + v.getDestination());
		 }
		
	}
	
	private void run1(String plate) {
		//New vehicle tries to enter from road3, port0 -> ENDPOINT
		VTService vtservice = VTService.getVTService();
		
		
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road3");
		 nVeh.setDestination("area3");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber(plate);
		 
		 vtservice.createVehicle(nVeh); 
		 System.out.println("New vehicle added, " + vehicleToString(nVeh)); 
	
		 return;
		
	}
	private void run2(String plate) {
		//New vehicle tries to enter from road3, port0 -> ENDPOINT
		VTService vtservice = VTService.getVTService();
		
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road4");
		 nVeh.setDestination("area4");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber(plate);
		 vtservice.createVehicle(nVeh);
		 System.out.println("New vehicle added, " + vehicleToString(nVeh));
		 return;
		
	}
	
	
	private static String vehicleToString(Vehicle v) {
		StringBuilder sb = new StringBuilder();
		sb.append("Vehicle ");
		sb.append(v.getPlateNumber());
		sb.append(" with ID " + v.getID() );		
		return sb.toString();
	}

}
