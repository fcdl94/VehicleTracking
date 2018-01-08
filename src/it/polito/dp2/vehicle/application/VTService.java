package it.polito.dp2.vehicle.application;

import java.util.HashMap;

import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.Vehicle;

public class VTService {

	private HashMap<String, Vehicle> vehicles;
	private GraphApp graphApp;
	
	//Singleton PATTERN
	private static VTService istance;
	private VTService() {
		vehicles = new HashMap<>();
		//probabilmente qui ci sarebbe da fare l'unmarshalling del file del modello (grafo + veicoli)
		Graph graph = new Graph(); //solo per sopprimere l'errore, in futuro deve prendere da unmarshalling
		graphApp = new GraphApp(graph);
	}
	public static VTService getVTService() {
		if(istance==null) {
			istance = new VTService();
		}
		return istance;
	}
	
		
	
}