package it.polito.dp2.vehicle.resource;

import java.math.BigInteger;
import java.net.URI;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.vehicle.application.VTService;
import it.polito.dp2.vehicle.model.Self;
import it.polito.dp2.vehicle.model.ObjectFactory;
import it.polito.dp2.vehicle.model.Vehicle;
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
			//this may be done also on node, using model/{node} but I preferred to put it here
			//get the vehicles of node "node"
			vehicles.getVehicle().addAll(vt.getVehiclesFromNode(node));
		}
		
		return objFactory.createVehicles(vehicles);
	}
	
	@POST 
    @ApiOperation(value = "create a new Vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 403, message = "Forbidden"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
    public JAXBElement<Vehicle> newVehicle(JAXBElement<Vehicle> jaxbvehicle, @Context UriInfo uriInfo) {
		
		Vehicle vehicle;
		
		if(jaxbvehicle==null || jaxbvehicle.getValue()==null) {
			throw new BadRequestException();
		}
		
		
		vehicle = vt.createVehicle(jaxbvehicle.getValue());
    	
		addLink(vehicle, uriInfo, false);
		
		return objFactory.createVehicle(vehicle);
	}
	
	@GET
	@Path("{id}")
    @ApiOperation(value = "get existing Vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces(MediaType.APPLICATION_XML)
    public JAXBElement<Vehicle> getVehicle(@PathParam("id") String id, @Context UriInfo uriInfo) {
		
		Vehicle vehicle;
		long lid;
		BigInteger bid;
		try {
			lid = Long.parseLong(id);
			bid = BigInteger.valueOf(lid);
		}
		catch(NumberFormatException ne){
			throw new NotFoundException();
		}
		
		vehicle = vt.getVehicle(bid);
		
		addLink(vehicle, uriInfo, true);
		
		return objFactory.createVehicle(vehicle);
	}
	
	@POST
	@Path("{id}")
    @ApiOperation(value = "update position of existing Vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 403, message = "Forbidden"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
	@Consumes("text/plain")
	@Produces(MediaType.APPLICATION_XML)
    public Response updateVehiclePosition(String nodeID, @PathParam("id") String id, @Context UriInfo uriInfo) {
		
		Vehicle vehicle;
		long lid;
		BigInteger bid;
		try {
			lid = Long.parseLong(id);
			bid = BigInteger.valueOf(lid);
		}
		catch(NumberFormatException ne){
			throw new NotFoundException();
		}
		
		if(vt.updateVehiclePosition(bid, nodeID)) {
			return Response.noContent().build();
		}
		else {
			vehicle = vt.getVehicle(bid);
			addLink(vehicle, uriInfo, true);
			return Response.ok(objFactory.createVehicle(vehicle)).build();
		}
	}
	
	@PUT
	@Path("{id}")
    @ApiOperation(value = "update of existing Vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 403, message = "Forbidden"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public JAXBElement<Vehicle>  updateVehicle(JAXBElement<Vehicle> jaxbvehicle, @PathParam("id") String id, @Context UriInfo uriInfo) {
		

		if(jaxbvehicle==null || jaxbvehicle.getValue()==null) {
			throw new BadRequestException();
		}
		
		Vehicle v;
		long lid;
		BigInteger bid;
		try {
			lid = Long.parseLong(id);
			bid = BigInteger.valueOf(lid);
		}
		catch(NumberFormatException ne){
			throw new NotFoundException();
		}
		
		v = vt.updateVehicle(bid, jaxbvehicle.getValue());
		addLink(v, uriInfo, true);
		return objFactory.createVehicle(v);
		
	}
	
	
	@DELETE
	@Path("{id}")
    @ApiOperation(value = "update of existing Vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No content"),
    		@ApiResponse(code = 403, message = "Forbidden"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
	public Response deleteVehicle(@PathParam("id") String id, @Context UriInfo uriInfo) {
		
		long lid;
		BigInteger bid;
		try {
			lid = Long.parseLong(id);
			bid = BigInteger.valueOf(lid);
		}
		catch(NumberFormatException ne){
			throw new NotFoundException();
		}
		//I must be sure that the model is validated
		if(!vt.deleteVehicle(bid)) {
			throw new ForbiddenException();
		}
		
		return Response.noContent().build();
		
	}
	
	
	private static void addLink(Vehicle v, UriInfo uriInfo, boolean isChildNode) {
		if(v.getSelf() == null) {
			Self l = new Self();
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			
			URI u ;
			if(!isChildNode) u = builder.path(v.getID().toString()).build();
			else u = builder.build();
			
			l.setHref(u.toString());
			
			v.setSelf(l);
		}
	}
	
	
}
