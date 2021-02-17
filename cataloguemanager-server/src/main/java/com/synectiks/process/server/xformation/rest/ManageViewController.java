package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.ManageView;
import com.synectiks.process.server.xformation.domain.ManageViewDetail;
import com.synectiks.process.server.xformation.service.ManageViewService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


//@Api(value = "Xformation/View", description = "Manage all xformation dashboard views")
//@Path("/xformation/view")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class ManageViewController{
//extends RestResource  {
//
//	private static final Logger LOG = LoggerFactory.getLogger(ManageViewController.class);
//	private final ManageViewService manageViewService;
//	
//	@Inject
//	public ManageViewController(ManageViewService manageViewService) {
//		this.manageViewService = manageViewService;
//	}
//	
//    @POST
//    @ApiOperation("Add a view")
//    public ManageView addView(@ApiParam("viewName") String viewName,
//    						@ApiParam("viewJson") String viewJson, 
//    						@ApiParam("description") String description,
//    						@ApiParam("type") String type,
//    						@ApiParam("status") String status) {
//    	LOG.info("Start controller addView");
//    	ManageView mv = this.manageViewService.addView(viewName, viewJson, description, type, status, status);
//    	LOG.info("End controller addView");
//    	return mv;
//    }
//    
//    @PUT
//    @ApiOperation("Update a view")
//    public ManageView updateView(@ApiParam("id") Long id,
//								@ApiParam("viewName") String viewName,
//	    						@ApiParam("viewJson") String viewJson, 
//	    						@ApiParam("description") String description,
//	    						@ApiParam("type") String type,
//	    						@ApiParam("status") String status) {
//    	LOG.info("Start controller updateView");
//    	ManageView mv = this.manageViewService.updateView(id, viewName, viewJson, description, type, status, status);
//    	LOG.info("End controller updateView");
//    	return mv;
//    }
//    
//    @DELETE
//    @Path("/{id}")
//    @ApiOperation("Delete a view")
//    public void deleteView(@ApiParam("id") Long id) {
//    	LOG.info("Start controller deleteView. ManageView id: "+id);
//    	this.manageViewService.deleteView(id);
//    	LOG.info("End controller deleteView. ManageView id: "+id);
//    }
//
//    @DELETE
//    @ApiOperation("Get all views")
//    public List<ManageViewDetail> listAllView() {
//    	LOG.info("Start controller listAllView");
//    	List<ManageViewDetail> list = this.manageViewService.listAllView();
//    	LOG.info("End controller listAllView");
//    	return list;
//    }
//
//    @GET
//    @Path("/{id}")
//    @ApiOperation("Get a views")
//    public ManageViewDetail getView(@ApiParam("id") Long id) {
//    	LOG.info("Start controller getView");
//    	ManageViewDetail mvd = this.manageViewService.getView(id);
//    	LOG.info("End controller getView");
//    	return mvd;
//    }

}
