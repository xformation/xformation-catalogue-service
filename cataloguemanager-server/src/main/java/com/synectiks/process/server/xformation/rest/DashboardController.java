package com.synectiks.process.server.xformation.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.rest.resources.streams.StreamResource;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Dashboard;
import com.synectiks.process.server.xformation.service.DashboardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@RequiresAuthentication
@Api(value = "Xformation/Dashboard", description = "Manage all xformation dashboards")
@Path("/xformation/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardController extends RestResource  {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);
	private final DashboardService dashboardService;
	
	@Inject
	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
    @GET
    @Path("/{id}")
    @ApiOperation("Get a dashboard for a given dashboard id")
    public Dashboard getDashboard(@ApiParam(name = "id") @PathParam("id") @NotBlank Long id) {
    	LOG.info("Start controller getDashboard");
//    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	Dashboard dashboard = this.dashboardService.getDashboard(id);
    	LOG.info("End controller getAlert. Alert id: "+id);
    	return dashboard;
//    	final URI streamUri = getUriBuilderToSelf().path(DashboardController.class)
//                .path("{id}")
//                .build(id);

//    	return Response.created().entity(dashboard).build();
    }
    
    @POST
    @Path("/addToCollector")
    @ApiOperation("Add a dashboard to a catalogue/collector")
    public Dashboard addToCollector( @ApiParam(name = "collectorId") @PathParam("collectorId") @NotBlank Long collectorId,
    								@ApiParam(name = "dashboardName") @PathParam("dashboardName") @NotBlank String dashboardName,
    								@ApiParam(name = "dashboardJson") @PathParam("dashboardJson") @NotBlank String dashboardJson,
    								@ApiParam(name = "dashboardDoc") @PathParam("dashboardDoc") String dashboardDoc) {
    	LOG.info("Start controller addToCollector");
//    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	Dashboard dashboard = this.dashboardService.createDashboard(collectorId, dashboardName, dashboardJson, dashboardDoc);
    	if(dashboard == null) {
    		LOG.error("Dashaboard cannot be added to collector. Collector id not found. Collector id: "+collectorId);
    		throw new NotFoundException("Dashaboard cannot be added to collector. Collector id not found. Collector " + collectorId + " doesn't exist");
    	}
    	LOG.info("End controller addToCollector");
    	return dashboard;
//        return Response.ok().entity(dashboard).build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation("Delete a dashboard")
    public void deleteDashboard(@ApiParam(name = "id") @PathParam("id") @NotBlank Long id) {
    	LOG.info("Start controller deleteDashboard. Dashboard id: "+id);
//    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	this.dashboardService.deleteDashboard(id);
    	LOG.info("End controller deleteDashboard. Dashboard id: "+id);
    }
    
    @GET
    @Path("/listDashboardOfCollector/{collectorId}")
    @ApiOperation("Get all the dashboards belongs to a collector")
    public List<Dashboard> listDashboardOfCollector(@ApiParam(name = "collectorId") @PathParam("collectorId") @NotBlank Long collectorId) {
    	LOG.info("Start controller listDashboardOfCollector. Collector id: "+collectorId);
//    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	List<Dashboard> list = this.dashboardService.listDashboardOfCollector(collectorId);
    	LOG.info("End controller listDashboardOfCollector. Collector id: "+collectorId);
    	
    	return list;
//    			Response.ok().entity(list).build();
    }

    @GET
    @Path("/listDashboard")
    @ApiOperation("Get all the dashboards")
    public List<CatalogDetail> listAllDashboard(
    		@ApiParam(name = "collectorId") @PathParam("collectorId") Long collectorId,
    		@ApiParam(name = "isFolder") @PathParam("isFolder") String isFolder) {
    	LOG.info("Start controller listAllDashboard");
//    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	List<CatalogDetail> catList = this.dashboardService.listAllDashboard(collectorId, isFolder);
    	LOG.info("End controller listAllDashboard");
    	return catList;
    }


    
    
}
