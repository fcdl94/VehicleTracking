package it.polito.dp2.vehicle.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Graph;
import it.polito.dp2.vehicle.model.ObjectFactory;

@Path("/model")
@Api(value = "/model")
public class ModelResource {
	private static VTService vt = VTService.getVTService();
	private static ObjectFactory objFactory = new ObjectFactory();
	
	@GET 
    @ApiOperation(value = "get the Model Graph")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<Graph> getVehicles(@Context UriInfo uriInfo) {
		
		return objFactory.createGraph(vt.getGraph());
	}
	
}
