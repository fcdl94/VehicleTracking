import java.io.File;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Vehicle;

public class ReadModel {

	public static void main(String[] args) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
		Unmarshaller um = jc.createUnmarshaller();
		
		 Model model = (Model) um.unmarshal( new File( "xsd/xml-gen.xml" ) );
		 
		 System.out.println("Marhsalling done!");
		 
		 VTService vtservice = VTService.getVTService();
		 
		 System.out.println("Get istance of VTService");
		 
		 vtservice.setModel(model);
		 
		 System.out.println("Loaded model in VTService");
		
		 for(Vehicle v : vtservice.getVehiclesFromNode("road3")) {
			 System.out.println("The vehicle " + v.getPlateNumber() + " have destination " + v.getDestination());
		 }
		
		 
		 
		//New vehicle tries to enter from road3, port0 -> ENDPOINT
		 Vehicle nVeh = new Vehicle();
		 NodeRef nNodeRef = new NodeRef();
		 nNodeRef.setNode("road3");
		 nNodeRef.setPort("Port0");
		 nVeh.setCurrentPosition(nNodeRef);
		 nVeh.setDestination("area3");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("VN1P2P0IT");
		 
		 if(vtservice.createVehicle(nVeh)) {
			 System.out.println("New vehicle added, " + vehicleToString(nVeh));
		 }
		 else {
			 System.out.println("Unable to add new vehicle");
		 }
		 
		 
		 for(Vehicle v : vtservice.getVehiclesFromNode("road3")) {
			 System.out.println("The vehicle " + v.getPlateNumber() + " have destination " + v.getDestination());
		 }
		
		 
	}
	
	private static String vehicleToString(Vehicle v) {
		StringBuilder sb = new StringBuilder();
		sb.append("Vehicle ");
		sb.append(v.getPlateNumber());
		sb.append(" with ID " + v.getID() );		
		return sb.toString();
	}

}
