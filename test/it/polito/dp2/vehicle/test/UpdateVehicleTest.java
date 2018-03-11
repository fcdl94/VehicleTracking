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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Path;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;

class UpdateVehicleTest {

	static Model model;
	
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
	void changePositionTestInPath() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		Path path;
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination("area2");
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
		
		path = nVeh.getPath();
		NodeRef nextNode = path.getNode().get(0).getTo();
		
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		nVeh.setCurrentPosition(nextNode.getNode());
		
		vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		System.out.println("Vehicle NOW goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		String actualNode = path.getNode().get(0).getFrom().getNode();
		
		assertEquals(nextNode.getNode(), actualNode);
		
		return;
	}
	
	@Test
	void changePositionTestNotInPath() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		Path path;
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination("area2");
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
		

		String nextNode = "road4";
		
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		nVeh.setCurrentPosition(nextNode);
		
		vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		path = nVeh.getPath();
		String actualNode = path.getNode().get(0).getFrom().getNode();
		
		assertEquals(nextNode, actualNode);
		
		return;
	}
	
	
	@Test
	void changeDestinatonTestWithPathExistent() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination("area2");
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
		
		String newDest = "road4";
		
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		nVeh.setDestination(newDest);
		
		nVeh = vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		System.out.println("Vehicle NOW goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		 assertEquals(newDest, nVeh.getDestination());
		
		return;
	}
	
	@Test
	void changeDestinatonTestWithPathNull() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		String destination = "area2";
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination(destination);
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
		
		String newDest = "road4";
		
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		nVeh.setCurrentPosition(destination);
		
		nVeh = vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		assertTrue(nVeh.getPath()==null);
		System.out.println("Vehicle arrived at " + destination);
		
		
		nVeh.setDestination(newDest);

		nVeh = vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		System.out.println("Vehicle NOW goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
		
		assertTrue(nVeh.getPath()!=null);

		
		return;
	}
	
	@Test
	void changePositionAndDestinationTest() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		String destination = "area2";
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination(destination);
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
		
		String newDest = "road4";
		String newPos = "road3";
		
		nVeh.setCurrentPosition(newPos);
		nVeh.setDestination(newDest);
		
		nVeh = vtservice.updateVehicle(nVeh.getID(), nVeh);
		
		System.out.println("Vehicle NOW goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
		
		assertEquals(nVeh.getPath().getNode().get(0).getFrom().getNode(), newPos);
	}

}
