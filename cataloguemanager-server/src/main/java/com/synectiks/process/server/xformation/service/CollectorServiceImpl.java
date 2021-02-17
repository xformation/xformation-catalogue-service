package com.synectiks.process.server.xformation.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
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
		String query = "select c from Collector c where c.id = :catalogId";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("catalogId", catalogId).getSingleResult();
		Catalog catalog = getCatalog(collector);
		LOG.info("End service getCatalog. Catalogue id: "+catalogId);
		return catalog;
	}
	
	@Override
	@Transactional
	public List<Catalog> getAllCollectors() {
		LOG.info("Start service getAllCollectors");
		List<Catalog> catalogList = new ArrayList<>();
		try {
			String query = "select c from Collector c";
			List<Collector> colList = entityManager.createQuery(query, Collector.class).getResultList();
			
			String dashboardQuery = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId";
			TypedQuery<Dashboard> tq = entityManager.createQuery(dashboardQuery, Dashboard.class);
			
			for (Collector o : colList) {
				LOG.info("Collector: " + o.toString());
				Catalog catalog = getCatalog(o, tq);
				LOG.info("Catalog: " + catalog.toString());
				catalogList.add(catalog);
			}
		}catch(Exception e) {
			LOG.error("Exception: ",e);
		}
		LOG.info("End service getAllCollectors");
		return catalogList;
	}
	
	private Catalog getCatalog(Collector collector) {
		LOG.info("Start service getCatalog(Collector collector)");
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
			catalogDetail.setId(db.getId());
			catalogDetail.setTitle(db.getName());
			catalogDetail.setDescription(db.getDescription());
			catalogDetail.setDashboardJson(new String(db.getDashboard()));
			catalogDetailList.add(catalogDetail);
		}
		catalog.setCatalogDetail(catalogDetailList);
		LOG.info("End service getCatalog(Collector collector)");
		return catalog;
	}
	
	private Catalog getCatalog(Collector collector, TypedQuery<Dashboard> tq) {
		LOG.info("Start service getCatalog (Collector collector, TypedQuery<Dashboard> tq)");
		Catalog catalog = new Catalog();
		catalog.setId(collector.getId());
		catalog.setCatalogName(collector.getName());
		catalog.setType(collector.getType());
		catalog.setCatalogDescription(collector.getDescription());
		
		List<Dashboard> dashboardList = tq.setParameter("collectorId", collector.getId()).getResultList();
		List<CatalogDetail> catalogDetailList = new ArrayList<>();
		for(Dashboard db: dashboardList) {
			LOG.info("Dashboard: " + db.toString());
			CatalogDetail catalogDetail = new CatalogDetail();
			catalogDetail.setId(db.getId());
			catalogDetail.setTitle(db.getName());
			catalogDetail.setDescription(db.getDescription());
			catalogDetail.setDashboardJson(new String(db.getDashboard()));
			catalogDetailList.add(catalogDetail);
		}
		catalog.setCatalogDetail(catalogDetailList);
		LOG.info("End service getCatalog (Collector collector, TypedQuery<Dashboard> tq)");
		return catalog;
	}
	
	@Transactional
	public synchronized void createCatalog(String name, String type, String description) {
		LOG.info("Start service createCatalog");
		Long maxId = getMax();
		Collector collector = new Collector();
		collector.setId(maxId+1);
        collector.setName(name);
        collector.setType(type);
        if(!StringUtils.isBlank(description)) {
        	collector.setDescription(description);
        }
//    	collector.setCreatedBy(userContext.getUser().getName());
//    	collector.setUpdatedBy(userContext.getUser().getName());
    	
    	Instant now = Instant.now();
    	Timestamp timestamp = Timestamp.from(now);
    	collector.setCreatedOn(timestamp);
    	collector.setUpdatedOn(timestamp);
    	LOG.info("Catalog to be created in database: "+collector.toString());
    	entityManager.persist(collector);
    	LOG.info("Catalog created in database");
//    	entityManager.refresh(collector);
//    	Catalog catalog = getCatalog(collector);
    	LOG.info("End service createCatalog");
	}
	
	@Transactional
	public void updateCatalog(Long catalogueId, String dataSource) {
		LOG.info("Start service updateCatalog. Catalogue id: "+catalogueId);
		
		String query = "select c from Collector c where c.id = :catalogueId";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("catalogueId", catalogueId).getSingleResult();
		
		collector.setDatasource(dataSource);
//    	collector.setUpdatedBy(userContext.getUser().getName());
    	Instant now = Instant.now();
    	Timestamp timestamp = Timestamp.from(now);
    	collector.setUpdatedOn(timestamp);
        
    	entityManager.merge(collector);
    	LOG.info("Catalog updated in database");
//    	entityManager.refresh(collector);
//    	Catalog catalog = getCatalog(collector);
    	LOG.info("End service updateCatalog. Catalogue id: "+catalogueId);
	}
	
	private synchronized Long getMax() {
		LOG.info("Start service getMax");
		String query = "select max(c.id) from Collector c ";
		Long maxId = entityManager.createQuery(query, Long.class).getSingleResult();
		LOG.info("Max Id: "+maxId);
		LOG.info("End service getMax");
		return maxId;
	}
}
