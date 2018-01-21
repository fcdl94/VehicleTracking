package it.polito.dp2.vehicle.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.ObjectFactory;
import it.polito.dp2.vehicle.model.Vehicles;

@Path("/vehicles")
@Api(value = "/vehicles")
public class VehicleResource {
	
	private static VTService vt = VTService.getVTService();
	private static ObjectFactory objFactory = new ObjectFactory();
	
	@GET 
    @ApiOperation(value = "get the Vehicles", notes = "Optionally get a node as text")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<Vehicles> getVehicles(@QueryParam("node") String node, @Context UriInfo uriInfo) {
		
		Vehicles vehicles = new Vehicles();
		if(node==null) {
			//get all vehicles
			vehicles.getVehicle().addAll(vt.getVehicles());
		}
		else {
			//get the vehicles of node "node"
			vehicles.getVehicle().addAll(vt.getVehiclesFromNode(node));
		}
		
		return objFactory.createVehicles(vehicles);
	}
	
	
	
	
}
