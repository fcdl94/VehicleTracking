package it.polito.dp2.vehicle.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

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
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.PathNode;
import it.polito.dp2.vehicle.model.Vehicle;
import it.polito.dp2.vehicle.model.Vehicles;

@TestInstance(Lifecycle.PER_CLASS)
class VTServiceTest {
	Model model;
	

	@SuppressWarnings("unchecked")
	@BeforeAll
	void unmarhsall() {
		JAXBContext jc;
		JAXBElement<Model> jaxbModel;
		try {
			jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
			Unmarshaller um = jc.createUnmarshaller();
			jaxbModel = (JAXBElement<Model>) um.unmarshal( new File( "xml/xml-test.xml" ) );
			model = jaxbModel.getValue();
			} catch (JAXBException e) {
			e.printStackTrace();
		}
		 System.out.println("Model made!");
		 
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

	
	@SuppressWarnings("unused")
	private static Model genModel() {
		Model model = new Model();
		
		Graph graph = new Graph();
		
		Node n = new Node();
		n.setID("node1");
		n.setName("Nodo 1");
		n.getPort().add("port1");
		n.getPort().add("port2");
		graph.getNode().add(n);
		
		n = new Node();
		n.setID("node2");
		n.setName("Nodo 2");
		n.getPort().add("port1");
		n.getPort().add("port2");
		graph.getNode().add(n);
		
		model.setGraph(graph);
		
		Vehicle v = new Vehicle();

		v.setCurrentPosition("node1");
		v.setDestination("node2");
		v.setID(BigInteger.valueOf(0));
		v.setPlateNumber("ABABABA");
		
		Vehicles vs = new Vehicles();
		vs.getVehicle().add(v);
		
		model.setVehicles(vs);
		return model;
	}
	
}
