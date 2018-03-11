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


class UpdateVehiclePositionTest {

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
	void testUpdateWithNodeInPath() {
		
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
		
		vtservice.updateVehiclePosition(nVeh.getID(), nextNode.getNode());
		
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		 }
		
		String actualNode = path.getNode().get(0).getFrom().getNode();
		
		assertEquals(nextNode.getNode(), actualNode);
		
		return;
	}
	
	@Test
	void testUpdateWithNodeNotInPath() {
		
		VTService vtservice = VTService.getVTService();
		
		Vehicle nVeh = new Vehicle();
		Path path1, path2;
		
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
		
		path1 = nVeh.getPath();
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
		
		//set a node out of the path (road2, road3, area2)
		vtservice.updateVehiclePosition(nVeh.getID(), "road5");
		
		path2 = nVeh.getPath();
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
				
		assertTrue( path1 != path2);
	}
	
	@Test
	void testOldPathDeleted() {
		
		VTService vtservice = VTService.getVTService();
		
		Vehicle nVeh = new Vehicle();
	
		nVeh.setCurrentPosition("road1");
		nVeh.setDestination("road2");
		nVeh.setID(BigInteger.valueOf(0));
		nVeh.setPlateNumber("NV1R1R2");
		
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
		
		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
		
		//set next node in the path (road1, road4, road2)
		vtservice.updateVehiclePosition(nVeh.getID(), "road4");

		System.out.println("Vehicle goes from: ");
		for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
		}
		
		nVeh = new Vehicle();		
		nVeh.setCurrentPosition("road1");
		nVeh.setDestination("road2");
		nVeh.setID(BigInteger.valueOf(0));
		nVeh.setPlateNumber("NV2R1R2");
		
		try {
			 nVeh = vtservice.createVehicle(nVeh);
			 System.out.println("Vehicle goes from: ");
			for(PathNode pn : nVeh.getPath().getNode()) {
				 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
			}
			 assertTrue( true );
			}
		catch (BadRequestException e){
				fail("Bad Request");
				return;
			}
		catch (ForbiddenException e) {
				fail("Forbidden");
			}
		
		
	}
	
}
