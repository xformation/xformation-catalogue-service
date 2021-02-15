package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.security.UserContext;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Catalog;
import com.synectiks.process.server.xformation.service.CollectorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@RequiresAuthentication
@Api(value = "Xformation/Collector", description = "Manage all xformation catalogues/collectors")
@Path("/xformation/catalogue")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CollectorController {

	private static final Logger LOG = LoggerFactory.getLogger(CollectorController.class);

	@GET
    @ApiOperation("List of all available catalogues/collectors")
    public List<Catalog> getAllCollectors() {
		LOG.info("Start controller getAllCollectors");
		CollectorService cs = GuiceInjectorHolder.getInjector().getInstance(CollectorService.class);
    	List<Catalog> list = cs.getAllCollectors();
        LOG.info("End controller getAllCollectors");
        return list;
    }

	@GET
    @Path("/{id}")
    @ApiOperation("Get a catalogue for a given catalogue id")
    public Catalog getCatalogue(@ApiParam(name = "catalogueId") @PathParam("catalogueId") @NotBlank Long id) {
    	LOG.info("Start controller getCatalogue. Catalogue id: "+id);
    	CollectorService cs = GuiceInjectorHolder.getInjector().getInstance(CollectorService.class);
    	Catalog catalog = cs.getCatalog(id);
    	LOG.info("End controller getCatalogue. Catalogue id: "+id);
    	return catalog;
    }
	
	@POST
    @ApiOperation("Create new catalogue")
    public List<Catalog> createCollector(@ApiParam(name = "name") @PathParam("name") @NotBlank String name, 
    		@ApiParam(name = "type") @PathParam("type") @NotBlank String type,
    		@ApiParam(name = "description") @PathParam("description") String description,
    		@Context UserContext userContext) {
		
		LOG.info("Start controller createCollector");
		LOG.debug(String.format("Collector name : %s, type : %s", name, type));
    	CollectorService cs = GuiceInjectorHolder.getInjector().getInstance(CollectorService.class);
    	cs.createCatalog(name, type, description, userContext);
    	List<Catalog> list = cs.getAllCollectors();
    	LOG.info("End controller createCollector");
    	return list;
    }

	@PUT
    @ApiOperation("Update a catalogue")
    public List<Catalog> updateCollector(@ApiParam(name = "catalogueId") @PathParam("catalogueId") @NotBlank Long catalogueId, 
    		@ApiParam(name = "dataSource") @PathParam("dataSource") @NotBlank String dataSource,
    		@Context UserContext userContext) {
		LOG.info("Start controller updateCollector");
		LOG.debug(String.format("Collector id : %d, data source : %s", catalogueId, dataSource));
    	CollectorService cs = GuiceInjectorHolder.getInjector().getInstance(CollectorService.class);
    	cs.updateCatalog(catalogueId, dataSource, userContext);
    	List<Catalog> list = cs.getAllCollectors();
    	LOG.info("End controller updateCollector");
    	return list;
    }

    
//    @DeleteMapping("/deleteCollector/{id}")
//    public ResponseEntity<Void> deleteCollector(@PathVariable Long id) {
//    	logger.info(String.format("Request to delete a Collector. Collector id : %d", id));
//    	Optional<Collector> oc = collectorRepository.findById(id);
//    	if(oc.isPresent()) {
//    		logger.debug("Deleting dashboards related to collector id: ",id);
//    		Dashboard dashboard = new Dashboard();
//        	dashboard.setCollector(oc.get());
//        	dashboardRepository.deleteAll(dashboardRepository.findAll(Example.of(dashboard)));
//        	
//        	logger.debug("Deleting library related to collector id: ",id);
//        	Library library = new Library();
//        	library.setCollector(oc.get());
//        	libraryRepository.deleteAll(libraryRepository.findAll(Example.of(library)));
//        	
//        	collectorRepository.deleteById(id);
//    	}
//    	return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
//    }
    

    



    
//    @GetMapping("/searchCollector")
//    public List<Collector> searchCollector(@RequestParam Map<String, String> criteriaMap) {
//        logger.debug("Request to get Collectors on given filter criteria");
//        Collector obj = new Collector();
//		boolean isFilter = false;
//		
//		if(criteriaMap.get("id") != null) {
//			obj.setId(Long.parseLong(criteriaMap.get("id")));
//    		isFilter = true;
//    	}
//		if(criteriaMap.get("name") != null) {
//			obj.setName(criteriaMap.get("name"));
//    		isFilter = true;
//    	}
//		if(criteriaMap.get("type") != null) {
//			obj.setType(criteriaMap.get("type"));
//    		isFilter = true;
//    	}
//		if(criteriaMap.get("datasource") != null) {
//			obj.setDatasource(criteriaMap.get("datasource"));
//    		isFilter = true;
//    	}
//		
//		List<Collector> list = null;
//    	if(isFilter) {
//    		list = this.collectorRepository.findAll(Example.of(obj), Sort.by(Direction.DESC, "id"));
//    	}else {
//    		list = this.collectorRepository.findAll(Sort.by(Direction.DESC, "id"));
//    	}
//        
//    	return list;
//    }
    
}
