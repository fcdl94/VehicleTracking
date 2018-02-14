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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;

@TestInstance(Lifecycle.PER_CLASS)
class CreateNewVehicleTest {
	
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
	void testGetPath() {
		
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
		 
		 String[] path = {"road3","area2"};
		 int i=0;
		 System.out.println("Vehicle goes from: ");
		 for(PathNode pn : nVeh.getPath().getNode()) {
			 System.out.println("\t Node " + pn.getFrom().getNode() + "  to " + pn.getTo().getNode());
			 assertEquals(path[i],pn.getTo().getNode());
			 i++;
		 }
		 return;
	}
	
	@Test
	void testBadRequestPath() {
		
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("");
		 nVeh.setDestination("area1");
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
	void testBadRequestPath2() {
		
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road1");
		 nVeh.setDestination("road2");
		 nVeh.setID(BigInteger.valueOf(0));
		 
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
		 nVeh.setCurrentPosition("road3");
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

	@Test
	void TestForbiddenFutureWalk() {
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road4");
		 nVeh.setDestination("road1");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("VEH1");
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);	
		 }
		 catch (BadRequestException e){
			 fail("it is not bad requested");
			 return;
		 }
		 catch (ForbiddenException e) {
			 fail("it is not forbidden");
			 return;
		 }
		 
		 nVeh = new Vehicle();
		 nVeh.setCurrentPosition("area1");
		 nVeh.setDestination("road1");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("VEH2");
		 try {
			 nVeh = vtservice.createVehicle(nVeh);
			 fail("it shoudl be forbidden");
		 }
		 catch (BadRequestException e){
			 fail("it is not bad requested");
			 return;
		 }
		 catch (ForbiddenException e) {
			 assertTrue(true);
			 return;
		 }
	}
	
	@Test
	void settingIdTest() {
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh = new Vehicle();
		 nVeh.setCurrentPosition("road4");
		 nVeh.setDestination("road1");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("VEH1");
	
		 //this is an approximation due to the test-model. In real cases it is not right
		 int indexCurr = vtservice.getVehicles().size();
		 
		 try {
			 nVeh = vtservice.createVehicle(nVeh);	
		 }
		 catch (BadRequestException e){
			 fail("it is not bad requested");
			 return;
		 }
		 catch (ForbiddenException e) {
			 fail("it is not forbidden");
			 return;
		 }
		 
		 assertEquals(indexCurr, nVeh.getID().intValue());
	}
	
	
	@Test
	void settingId2Test() {
		VTService vtservice = VTService.getVTService();
		 Vehicle nVeh1 = new Vehicle();
		 nVeh1.setCurrentPosition("road4");
		 nVeh1.setDestination("road1");
		 nVeh1.setID(BigInteger.valueOf(0));
		 nVeh1.setPlateNumber("VEH1");
	
		 Vehicle nVeh2 = new Vehicle();
		 nVeh2.setCurrentPosition("road2");
		 nVeh2.setDestination("road4");
		 nVeh2.setID(BigInteger.valueOf(0));
		 nVeh2.setPlateNumber("VEH2");
		 
		 //this is an approximation due to the test-model. In real cases it is not right
		 int indexCurr = vtservice.getVehicles().size();
		 
		 try {
			 nVeh1 = vtservice.createVehicle(nVeh1);
			 nVeh2 = vtservice.createVehicle(nVeh2);
		 }
		 catch (BadRequestException e){
			 fail("it is not bad requested");
			 return;
		 }
		 catch (ForbiddenException e) {
			 fail("it is not forbidden");
			 return;
		 }
		 
		 assertEquals(indexCurr, nVeh1.getID().intValue());
		 assertEquals(indexCurr + 1, nVeh2.getID().intValue());
		 
	}
	
}
