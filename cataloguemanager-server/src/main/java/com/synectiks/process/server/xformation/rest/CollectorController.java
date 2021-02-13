package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Catalog;
import com.synectiks.process.server.xformation.service.CollectorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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

//	private Catalog createCatalog(Collector collector) {
//		Catalog catalog = new Catalog();
//		catalog.setId(collector.getId());
//		catalog.setCatalogName(collector.getName());
//		catalog.setType(collector.getType());
//		catalog.setCatalogDescription(collector.getDescription());
//		
//		Dashboard dashboard = new Dashboard();
//		dashboard.setCollector(collector);
//		List<Dashboard> dashboardList = dashboardRepository.findAll(Example.of(dashboard));
//		List<CatalogDetail> catalogDetailList = new ArrayList<>();
//		for(Dashboard db: dashboardList) {
//			CatalogDetail catalogDetail = new CatalogDetail();
//			catalogDetail.setTitle(db.getName());
//			catalogDetail.setDescription(db.getDescription());
//			catalogDetail.setDashboardJson(new String(db.getDashboard()));
//			catalogDetailList.add(catalogDetail);
//		}
//		catalog.setCatalogDetail(catalogDetailList);
//		return catalog;
//	}

//    @PostMapping("/addCollector")
//    public ResponseEntity<List<Catalog>> addCollector(@RequestParam String name, 
//    		@RequestParam String type,
//    		@RequestParam(required = false) String description,
//    		@RequestParam (name = "userName", required = false) String userName) throws URISyntaxException {
//        logger.info(String.format("Request to create a Collector. Collector name : %s, type : %s", name, type));
//    	Collector collector = new Collector();
//        collector.setName(name);
//        collector.setType(type);
//        collector.setDescription(description);
//        
//        if(!StringUtils.isBlank(userName)) {
//        	collector.setCreatedBy(userName);
//        	collector.setUpdatedBy(userName);
//    	}else {
//    		collector.setCreatedBy(Constants.SYSTEM_ACCOUNT);
//    		collector.setUpdatedBy(Constants.SYSTEM_ACCOUNT);
//    	}
//    	Instant now = Instant.now();
//    	collector.setCreatedOn(now);
//    	collector.setUpdatedOn(now);
//        
//    	
//        collector = collectorRepository.save(collector);
//        List<Catalog> list = getAllCollectors();
//        return ResponseEntity.created(new URI("/api/addCollector/" + collector.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, collector.getId().toString()))
//            .body(list);
//        
//    }


//    @PutMapping("/updateCollector")
//    public ResponseEntity<Collector> updateCollector(@RequestParam Long id, @RequestParam String dataSource) throws URISyntaxException {
//        logger.info(String.format("Request to update a Collector. Collector id : %d, datasource : %s", id, dataSource));
//        
//        Collector collector = new Collector();
//        collector.setId(id);
//        collector.setDatasource(dataSource);
//        
//        collector = collectorRepository.save(collector);
//        
//        return ResponseEntity.created(new URI("/api/updateCollector/" + collector.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, collector.getId().toString()))
//            .body(collector);
//    }
    
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
    

    

//    @GetMapping("/getCollector/{id}")
//    public ResponseEntity<Catalog> getCollector(@PathVariable Long id) throws URISyntaxException {
//        logger.debug("Request to get a Collector. Collector id : ", id);
//        Collector collector = collectorRepository.findById(id).get();
//        Catalog catalog = createCatalog(collector);
//        return ResponseEntity.created(new URI("/api/listCollector/" + collector.getId()))
//                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, collector.getId().toString()))
//                .body(catalog);
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
