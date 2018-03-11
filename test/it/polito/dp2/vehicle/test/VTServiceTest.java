package it.polito.dp2.vehicle.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;

@TestInstance(Lifecycle.PER_CLASS)
class VTServiceTest {
	
	private static Model model;

	@SuppressWarnings("unchecked")
	@BeforeAll
	static void makeModel() {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
			Unmarshaller um = jc.createUnmarshaller();
			JAXBElement<Model> jmod;
			jmod = (JAXBElement<Model>) um.unmarshal( new File( "xml/xml-test.xml" ) );
			model = jmod.getValue();
			
			} catch (JAXBException e) {
			e.printStackTrace();
		}
		System.out.println("Model made!");
	}
	
	@BeforeEach
	void startup() {
		VTService vtservice = VTService.getVTService();
		vtservice.setModel(model);
	}
	
	@Test
	void loadModelTest() {
				
		VTService vtservice = VTService.getVTService();
	
		List<Vehicle> vl = vtservice.getVehiclesFromNode("area2");
		
		assertEquals(vl.size(), 2);

		assertTrue(vl.get(0).getPlateNumber().equals("V3P6IT"));
	}
	
	@Test
	void getAllVehicles() {
		VTService vtservice = VTService.getVTService();
		List<Vehicle> vs = vtservice.getVehicles();
		
		int flag = 0;
		 for(Vehicle v : vs) {
			 System.out.println("Vehicle " + v.getPlateNumber() + " is present");
			 if(v.getPlateNumber().equals("V3P6IT")) {
				 flag++;
			}
		 }
		 
		assertTrue(flag==1);
	}

	@Test
	void loadAddVehicle() {
		VTService vtservice = VTService.getVTService();
		
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road1");
		 nVeh.setDestination("road2");
		 //nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("ABABAB");
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);
			 System.out.println("New vehicle added, " + nVeh.getPlateNumber());
		 }
		 catch (BadRequestException e){
			 System.out.println("Unable to add new vehicle - BAD REQUEST");
			 return;
		 }
		 catch (ForbiddenException e) {
			 System.out.println("Unable to add new vehicle - FORBIDDEN");
			 return;
		 }
		 
		 System.out.println("Vehicle goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		 
		boolean flag = false;
		 for(Vehicle v : vtservice.getVehiclesFromNode("road1")) {
			 if(v.getPlateNumber().equals(nVeh.getPlateNumber())) {
				 flag = true;
				 }
		 }
		 
		 assertTrue(flag);
		 
		 return;
	}	
}
