package com.synectiks.process.server.xformation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.alerts.AlertServiceImpl;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Catalog;
import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Collector;
import com.synectiks.process.server.xformation.domain.Dashboard;

public class CollectorServiceImpl implements CollectorService {

private static final Logger LOG = LoggerFactory.getLogger(AlertServiceImpl.class);

	
	private EntityManager entityManager = null;

	@Inject
    public CollectorServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
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
				Catalog catalog = createCatalog(o);
	        	catalogList.add(catalog);
			}
		}catch(Exception e) {
			LOG.error("Exception: ",e);
		}
		LOG.info("End service getAllCollectors");
		return catalogList;
	}
	
	private Catalog createCatalog(Collector collector) {
		LOG.info("Start service createCatalog");
		Catalog catalog = new Catalog();
		catalog.setId(collector.getId());
		catalog.setCatalogName(collector.getName());
		catalog.setType(collector.getType());
		catalog.setCatalogDescription(collector.getDescription());
		
		String query = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId";
		
		List<Dashboard> dashboardList = entityManager.createQuery(query, Dashboard.class).setParameter("collectorId", collector.getId()).getResultList();
		List<CatalogDetail> catalogDetailList = new ArrayList<>();
		for(Dashboard db: dashboardList) {
			LOG.info("Dashboard: " + db.toString());
			CatalogDetail catalogDetail = new CatalogDetail();
			catalogDetail.setTitle(db.getName());
			catalogDetail.setDescription(db.getDescription());
			catalogDetail.setDashboardJson(new String(db.getDashboard()));
			catalogDetailList.add(catalogDetail);
		}
		catalog.setCatalogDetail(catalogDetailList);
		LOG.info("End service createCatalog");
		return catalog;
	}
}
