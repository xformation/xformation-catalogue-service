package com.synectiks.process.server.xformation.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.security.UserContext;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Catalog;
import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Collector;
import com.synectiks.process.server.xformation.domain.Dashboard;

public class CollectorServiceImpl implements CollectorService {

	private static final Logger LOG = LoggerFactory.getLogger(CollectorServiceImpl.class);
	private EntityManager entityManager = null;

	@Inject
    public CollectorServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }

	@Override
	public Catalog getCatalog(Long catalogId) {
		LOG.info("Start service getCatalog. Catalogue id: "+catalogId);
		String query = "select c from Collector c where c.id =: catalogId";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("catalogId", catalogId).getSingleResult();
		Catalog catalog = getCatalog(collector);
		LOG.info("End service getCatalog. Catalogue id: "+catalogId);
		return catalog;
	}
	
	@Override
	public List<Catalog> getAllCollectors() {
		LOG.info("Start service getAllCollectors");
		List<Catalog> catalogList = Collections.emptyList();
		try {
			String query = "select c from Collector c";
			List<Collector> colList = entityManager.createQuery(query, Collector.class).getResultList();
			for (Collector o : colList) {
				LOG.debug("Collector: " + o.toString());
				Catalog catalog = getCatalog(o);
	        	catalogList.add(catalog);
			}
		}catch(Exception e) {
			LOG.error("Exception: ",e);
		}
		LOG.info("End service getAllCollectors");
		return catalogList;
	}
	
	private Catalog getCatalog(Collector collector) {
		LOG.info("Start service getCatalog");
		Catalog catalog = new Catalog();
		catalog.setId(collector.getId());
		catalog.setCatalogName(collector.getName());
		catalog.setType(collector.getType());
		catalog.setCatalogDescription(collector.getDescription());
		
		String query = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId";
		
		List<Dashboard> dashboardList = entityManager.createQuery(query, Dashboard.class).setParameter("collectorId", collector.getId()).getResultList();
		List<CatalogDetail> catalogDetailList = new ArrayList<>();
		for(Dashboard db: dashboardList) {
			LOG.debug("Dashboard: " + db.toString());
			CatalogDetail catalogDetail = new CatalogDetail();
			catalogDetail.setTitle(db.getName());
			catalogDetail.setDescription(db.getDescription());
			catalogDetail.setDashboardJson(new String(db.getDashboard()));
			catalogDetailList.add(catalogDetail);
		}
		catalog.setCatalogDetail(catalogDetailList);
		LOG.info("End service getCatalog");
		return catalog;
	}
	
	public Catalog createCatalog(String name, String type, String description, UserContext userContext) {
		LOG.info("Start service createCatalog");
		Collector collector = new Collector();
        collector.setName(name);
        collector.setType(type);
        collector.setDescription(description);
        
    	collector.setCreatedBy(userContext.getUser().getName());
    	collector.setUpdatedBy(userContext.getUser().getName());
    	
    	Instant now = Instant.now();
    	collector.setCreatedOn(now);
    	collector.setUpdatedOn(now);
        
    	entityManager.persist(collector);
    	LOG.debug("Catalog created in database");
    	entityManager.refresh(collector);
    	Catalog catalog = getCatalog(collector);
    	LOG.info("End service createCatalog");
		return catalog;
	}
	
	public Catalog updateCatalog(Long id, String dataSource, UserContext userContext) {
		LOG.info("Start service updateCatalog");
		String query = "select c from Collector c where c.id =: id";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("id", id).getSingleResult();
		collector.setDatasource(dataSource);
    	collector.setUpdatedBy(userContext.getUser().getName());
    	Instant now = Instant.now();
    	collector.setUpdatedOn(now);
        
    	entityManager.merge(collector);
    	LOG.debug("Catalog updated in database");
    	entityManager.refresh(collector);
    	Catalog catalog = getCatalog(collector);
    	LOG.info("End service updateCatalog");
		return catalog;
	}
	
}
