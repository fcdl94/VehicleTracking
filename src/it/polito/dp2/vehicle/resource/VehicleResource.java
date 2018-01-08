package it.polito.dp2.vehicle.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.vehicle.application.VTService;

@Path("/vehicles")
@Api(value = "/vehicles")
public class VehicleResource {
	
	VTService vt = VTService.getVTService();
	
	@GET 
    @ApiOperation(	value = "get the Vehicles", notes = "gets a string list of nodes and a string list of plates")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces(MediaType.APPLICATION_XML)
    public void getVehicles( @Context UriInfo uriInfo) {
		
		
	}
	
}
