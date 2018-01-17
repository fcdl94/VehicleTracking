package it.polito.dp2.vehicle.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import it.polito.dp2.vehicle.application.GraphApp;
import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Model;
import it.polito.dp2.vehicle.model.Node;
import it.polito.dp2.vehicle.model.NodeRef;
import it.polito.dp2.vehicle.model.Vehicle;
import it.polito.dp2.vehicle.model.Vehicles;

@TestInstance(Lifecycle.PER_CLASS)
class VTServiceTest {
	Model model;
	

	@BeforeAll
	void unmarhsall() {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance( "it.polito.dp2.vehicle.model" );
			Unmarshaller um = jc.createUnmarshaller();
			model = (Model) um.unmarshal( new File( "xsd/xml-gen.xml" ) );
			
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
	
		List<Vehicle> vl = vtservice.getVehiclesFromNode("road1");
		
		//dependent by the MODEL!!
		assertEquals(vl.size(), 2);

		assertTrue(vl.get(0).getPlateNumber().equals("V4P0IT"));
	}
	
	@Test
	void getAllVehicles() {
		VTService vtservice = VTService.getVTService();
		List<Vehicle> vs = vtservice.getVehicles();
		
		
		int flag = 0;
		 for(Vehicle v : vs) {
			 System.out.println("Vehicle " + v.getPlateNumber() + " is present");
			 if(v.getPlateNumber().equals("V8P10IT")) {
				 flag++;
				 }
		 }
		 
		 
		 
		assertTrue(flag==1);
	}

	@Test
	void loadAddVehicle() {
		VTService vtservice = VTService.getVTService();
		
		 Vehicle nVeh = new Vehicle();
		 NodeRef nNodeRef = new NodeRef();
		 nNodeRef.setNode("road3");
		 nNodeRef.setPort("Port0");
		 nVeh.setCurrentPosition(nNodeRef);
		 nVeh.setDestination("area3");
		 nVeh.setID(BigInteger.valueOf(0));
		 nVeh.setPlateNumber("ABABAB");
		 
		 try {
		 nVeh = vtservice.createVehicle(nVeh);
		 System.out.println("New vehicle added, " + nVeh.getPlateNumber());
		 }
		 catch (BadRequestException e){
			 System.out.println("Unable to add new vehicle");
		 }
		 catch (ForbiddenException e) {
			 System.out.println("Unable to add new vehicle");
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
		NodeRef nr = new NodeRef();
		nr.setNode("node1");
		nr.setPort("port1");
		
		v.setCurrentPosition(nr);
		v.setDestination("node2");
		v.setID(BigInteger.valueOf(0));
		v.setPlateNumber("ABABABA");
		
		Vehicles vs = new Vehicles();
		vs.getVehicle().add(v);
		
		model.setVehicles(vs);
		return model;
	}
	
}
