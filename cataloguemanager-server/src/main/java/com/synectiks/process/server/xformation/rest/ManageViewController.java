package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.ManageView;
import com.synectiks.process.server.xformation.domain.ManageViewDetail;
import com.synectiks.process.server.xformation.service.ManageViewService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(value = "Xformation/View", description = "Manage all xformation dashboard views")
@Path("/xformation/view")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManageViewController {

	private static final Logger LOG = LoggerFactory.getLogger(ManageViewController.class);

    @POST
    @ApiOperation("Add a view")
    public Response addView(@ApiParam("viewName") String viewName,
    						@ApiParam("viewJson") String viewJson, 
    						@ApiParam("description") String description,
    						@ApiParam("type") String type,
    						@ApiParam("status") String status) {
    	LOG.info("Start controller addView");
    	ManageViewService mvs = GuiceInjectorHolder.getInjector().getInstance(ManageViewService.class);
    	ManageView mv = mvs.addView(viewName, viewJson, description, type, status, status);
    	LOG.info("End controller addView");
    	return Response.ok().entity(mv).build();
    }
    
    @PUT
    @ApiOperation("Update a view")
    public Response updateView(	@ApiParam("id") Long id,
								@ApiParam("viewName") String viewName,
	    						@ApiParam("viewJson") String viewJson, 
	    						@ApiParam("description") String description,
	    						@ApiParam("type") String type,
	    						@ApiParam("status") String status) {
    	LOG.info("Start controller updateView");
    	ManageViewService mvs = GuiceInjectorHolder.getInjector().getInstance(ManageViewService.class);
    	ManageView mv = mvs.updateView(id, viewName, viewJson, description, type, status, status);
    	LOG.info("End controller updateView");
    	return Response.ok().entity(mv).build();
    }
    
    @DELETE
    @Path("/{id}")
    @ApiOperation("Delete a view")
    public void deleteView(@ApiParam("id") Long id) {
    	LOG.info("Start controller deleteView. ManageView id: "+id);
    	ManageViewService ds = GuiceInjectorHolder.getInjector().getInstance(ManageViewService.class);
    	ds.deleteView(id);
    	LOG.info("End controller deleteView. ManageView id: "+id);
    }

    @DELETE
    @ApiOperation("Get all views")
    public List<ManageViewDetail> listAllView() {
    	LOG.info("Start controller listAllView");
    	ManageViewService ds = GuiceInjectorHolder.getInjector().getInstance(ManageViewService.class);
    	List<ManageViewDetail> list = ds.listAllView();
    	LOG.info("End controller listAllView");
    	return list;
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Get a views")
    public Response getView(@ApiParam("id") Long id) {
    	LOG.info("Start controller getView");
    	ManageViewService ds = GuiceInjectorHolder.getInjector().getInstance(ManageViewService.class);
    	ManageViewDetail mvd = ds.getView(id);
    	LOG.info("End controller getView");
    	return Response.ok().entity(mvd).build();
    }

}
