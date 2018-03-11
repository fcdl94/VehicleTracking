package it.polito.dp2.vehicle.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

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
import it.polito.dp2.vehicle.model.Vehicle;

class DeleteTest {


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
	void correctDeleteTest() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination("road3");
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
		
		//System.out.println("Supposing vehicle arrives to endpoint");
		
		vtservice.updateVehiclePosition(nVeh.getID(), "road3");
		
		
		if( vtservice.deleteVehicle(nVeh.getID()) ) {
			assertTrue(true);
		}
		else fail("Delete not allowed");
		
	}
	
	@Test
	void incorrectPositionDeleteTest() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		nVeh.setCurrentPosition("road3");
		nVeh.setDestination("road2");
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
		
		//System.out.println("Supposing vehicle arrives to endpoint");
		
		vtservice.updateVehiclePosition(nVeh.getID(), "road2");
		
		
		if( vtservice.deleteVehicle(nVeh.getID()) ) {
			fail("Should be not allowed to exit");
		}
		else assertTrue(true);
		
	}
	
	@Test
	void notInPositionDeleteTest() {
		VTService vtservice = VTService.getVTService();
		Vehicle nVeh = new Vehicle();
		
		nVeh.setCurrentPosition("road2");
		nVeh.setDestination("road3");
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
				
		if( vtservice.deleteVehicle(nVeh.getID()) ) {
			fail("Should be not allowed to exit");
		}
		else assertTrue(true);
		
	}

}
