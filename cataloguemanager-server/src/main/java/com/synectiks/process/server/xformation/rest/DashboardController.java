package com.synectiks.process.server.xformation.rest;

import java.util.List;

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

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
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
public class DashboardController {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);
    
    @GET
    @Path("/{id}")
    @ApiOperation("Get a dashboard for a given dashboard id")
    public Response getDashboard(@ApiParam(name = "id") @PathParam("id") @NotBlank Long id) {
    	LOG.info("Start controller getDashboard");
    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	Dashboard dashboard = ds.getDashboard(id);
    	LOG.info("End controller getAlert. Alert id: "+id);
    	return Response.ok().entity(dashboard).build();
    }
    
    @POST
    @Path("/addToCollector")
    @ApiOperation("Add a dashboard to a catalogue/collector")
    public Response addToCollector( @ApiParam(name = "collectorId") @PathParam("collectorId") @NotBlank Long collectorId,
    								@ApiParam(name = "dashboardName") @PathParam("dashboardName") @NotBlank String dashboardName,
    								@ApiParam(name = "dashboardJson") @PathParam("dashboardJson") @NotBlank String dashboardJson,
    								@ApiParam(name = "dashboardDoc") @PathParam("dashboardDoc") String dashboardDoc) {
    	LOG.info("Start controller addToCollector");
    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	Dashboard dashboard = ds.createDashboard(collectorId, dashboardName, dashboardJson, dashboardDoc);
    	if(dashboard == null) {
    		LOG.error("Dashaboard cannot be added to collector. Collector id not found. Collector id: "+collectorId);
    		throw new NotFoundException("Dashaboard cannot be added to collector. Collector id not found. Collector " + collectorId + " doesn't exist");
    	}
    	LOG.info("End controller addToCollector");
        return Response.ok().entity(dashboard).build();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation("Delete a dashboard")
    public void deleteDashboard(@ApiParam(name = "id") @PathParam("id") @NotBlank Long id) {
    	LOG.info("Start controller deleteDashboard. Dashboard id: "+id);
    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	ds.deleteDashboard(id);
    	LOG.info("End controller deleteDashboard. Dashboard id: "+id);
    }
    
    @GET
    @Path("/listDashboardOfCollector/{collectorId}")
    @ApiOperation("Get all the dashboards belongs to a collector")
    public Response listDashboardOfCollector(@ApiParam(name = "collectorId") @PathParam("collectorId") @NotBlank Long collectorId) {
    	LOG.info("Start controller listDashboardOfCollector. Collector id: "+collectorId);
    	DashboardService ds = GuiceInjectorHolder.getInjector().getInstance(DashboardService.class);
    	List<Dashboard> list = ds.listDashboardOfCollector(collectorId);
    	LOG.info("End controller listDashboardOfCollector. Collector id: "+collectorId);
    	return Response.ok().entity(list).build();
    }

//    @GetMapping("/listDashboard")
//    public List<CatalogDetail> listAllDashboard(@RequestParam(required = false)  Long id, @RequestParam(required = false)  String isFolder) {
//        if(!StringUtils.isBlank(isFolder) && !Objects.isNull(id)) {
//        	logger.info("Request to get dashboards for id : "+id);
//        	if(Boolean.valueOf(isFolder)) {
//        		logger.info("Getting all dashboards of the given collector id : "+id);
//        		Collector col = collectorRepository.findById(id).get();
//        		Dashboard dashboard = new Dashboard();
//        		dashboard.setCollector(col);
//        		List<Dashboard> dashList = dashboardRepository.findAll(Example.of(dashboard));
//        		List<CatalogDetail> catList = new ArrayList<>();
//        		for(Dashboard d: dashList) {
//        			CatalogDetail cd = new CatalogDetail();
//        			cd.setId(d.getId());
//        			cd.setTitle(d.getName());
//        			cd.setDescription(d.getDescription());
//        			cd.setDashboardJson(new String(d.getDashboard()));
//        			catList.add(cd);
//        		}
//        		return catList;
//        	}else {
//        		logger.info("Getting a dashboard of the given id : "+id);
//        		Dashboard d = dashboardRepository.findById(id).get();
//        		CatalogDetail cd = new CatalogDetail();
//    			cd.setId(d.getId());
//    			cd.setTitle(d.getName());
//    			cd.setDescription(d.getDescription());
//    			cd.setDashboardJson(new String(d.getDashboard()));
//    			List<CatalogDetail> list = new ArrayList<>();
//        		list.add(cd);
//        		return list;
//        	}
//        }
//    	logger.info("Request to get all dashboards");
//        List<Dashboard> dashList =  dashboardRepository.findAll(Sort.by(Direction.DESC, "id"));
//        List<CatalogDetail> catList = new ArrayList<>();
//		for(Dashboard d: dashList) {
//			CatalogDetail cd = new CatalogDetail();
//			cd.setId(d.getId());
//			cd.setTitle(d.getName());
//			cd.setDescription(d.getDescription());
//			cd.setDashboardJson(new String(d.getDashboard()));
//			catList.add(cd);
//		}
//		return catList;
//    }


    
    
}
