package it.polito.dp2.vehicle.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigInteger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;

@TestInstance(Lifecycle.PER_CLASS)
class PathTest {
	
	static Model model;
	
	@BeforeAll
	void startup() {
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
		VTService vtservice = VTService.getVTService();
		vtservice.setModel(model);
	}

	@Test
	void testGetPath() {
		
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 NodeRef nNodeRef = new NodeRef();
		 nNodeRef.setNode("road1");
		 nNodeRef.setPort("Port0");
		 nVeh.setCurrentPosition(nNodeRef);
		 nVeh.setDestination("road2");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("NVR1R2");
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);
		 }
		 catch (BadRequestException e){
			 fail("Bad Request");
			 return;
		 }
		 catch (ForbiddenException e) {
			 fail("Forbidden");
		 }
		 
		 String[] path = {"road4","road2"};
		 int i=0;
		 System.out.println("Vehicle goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
			 //assertEquals(path[i],pn.getTo().getNode());
			 i++;
		 }
		 return;
	}
	
	@Test
	void testBadRequestPath() {
		
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 NodeRef nNodeRef = new NodeRef();
		 nVeh.setCurrentPosition(nNodeRef);
		 nVeh.setDestination("road2");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("NVR1R2");
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);
			 fail("Bad Request must be raised");
		 }
		 catch (BadRequestException e){
			 assertTrue(true);
		 }
		 catch (ForbiddenException e) {
			 fail("Forbidden but Bad Request must be raised");
		 }

		 return;
	}
	
	@Test
	void testForbiddenPath() {
		
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 NodeRef nNodeRef = new NodeRef();
		 nNodeRef.setNode("road3");
		 nNodeRef.setPort("Port0");
		 nVeh.setCurrentPosition(nNodeRef);
		 nVeh.setDestination("road5");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("ABABAB");
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);
			 fail("Exception must be raised");
		 }
		 catch (BadRequestException e){
			 fail("it is not bad request but Forbidden");
			 return;
		 }
		 catch (ForbiddenException e) {
			 assertTrue(true);
			 return;
		 }
		 
		 System.out.println("Vehicle goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		 
		boolean flag = false;
		 for(Vehicle v : vtservice.getVehiclesFromNode("road3")) {
			 if(v.getPlateNumber().equals(v.getPlateNumber())) {
				 flag = true;
				 }
		 }
		 
		 assertTrue(flag);
		 
		 return;
	}

}
