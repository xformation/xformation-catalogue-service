package com.synectiks.process.server.xformation.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Catalog;
import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Collector;
import com.synectiks.process.server.xformation.domain.Dashboard;

public class DashboardServiceImpl implements DashboardService {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);
	private EntityManager entityManager = null;

	@Inject
    public DashboardServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
	@Override
	public Dashboard getDashboard(Long dashboardId) {
		LOG.info("Start service getDashboard. Dashboard id: "+dashboardId);
		String query = "select d from Dashboard d where d.id = :dashboardId";
		Dashboard dashboard = entityManager.createQuery(query, Dashboard.class).setParameter("dashboardId", dashboardId).getSingleResult();
		LOG.info("End service getDashboard. Dashboard id: "+dashboardId);
		return dashboard;
	}
	
	@Override
	@Transactional
	public Dashboard createDashboard(Long collectorId, String dashboardName, String dashboardJson, String dashboardDoc) {
		
		LOG.info("Start service createDashboard");
		String query = "select c from Collector c where c.id = :collectorId";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("collectorId", collectorId).getSingleResult();
		if(collector == null) {
			LOG.warn("Collector not found. Returning null. Collector Id: "+collectorId);
			return null;
		}
		Long maxId = getMax();
		Dashboard dashboard = new Dashboard();
		dashboard.setId(maxId+1);
        dashboard.setCollector(collector);
        dashboard.setName(dashboardName);
        dashboard.setDashboard(dashboardJson.getBytes());
        if(!StringUtils.isBlank(dashboardDoc)) {
        	dashboard.setDescription(dashboardDoc);
        }
        
//    	dashboard.setCreatedBy(userContext.getUserId());
//		dashboard.setUpdatedBy(userContext.getUserId());
    	Instant now = Instant.now();
    	Timestamp timestamp = Timestamp.from(now);
    	dashboard.setCreatedOn(timestamp);
    	dashboard.setUpdatedOn(timestamp);
    	entityManager.persist(dashboard);
    	LOG.debug("Dashboard created in database");
		LOG.info("End service createDashboard");
		return dashboard;
	}
	
	@Override
	@Transactional
	public void deleteDashboard(Long dashboardId) {
		LOG.info("Start service deleteDashboard. Dashboard id: "+dashboardId);
		Dashboard dashboard = getDashboard(dashboardId);
		if (dashboard != null) {
			entityManager.remove(dashboard);
			LOG.debug("Dashboard deleted from database");
		} else {
			LOG.warn("No dashboard found in database for delete");
		}
		LOG.info("End service deleteAlert. Dashboard id: "+dashboardId);
	}
	
	@Override
	public List<Dashboard> listDashboardOfCollector(Long collectorId) {
		LOG.info("Start service listDashboardOfCollector. Collector id: "+collectorId);
		String query = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId order by d.id desc";
		List<Dashboard> dashboardList = entityManager.createQuery(query, Dashboard.class).setParameter("collectorId", collectorId).getResultList();
		
		if(dashboardList == null || (dashboardList != null && dashboardList.size() == 0)) {
			LOG.warn("Collector not found. Returning empty dashboard list. Collector Id: "+collectorId);
			return Collections.emptyList();
		}
		LOG.info("End service listDashboardOfCollector. Collector id: "+collectorId);
		return dashboardList;
    }
	
	@Override
	public List<Dashboard> getAllDashboards() {
		LOG.info("Start service getAllDashboards");
		String query = "select d from Dashboard d order by d.id DESC";
		List<Dashboard> dashboardList = entityManager.createQuery(query, Dashboard.class).getResultList();
		LOG.info("End service getAllDashboards");
		return dashboardList;
	}
	
	@Override
	public List<CatalogDetail> listAllDashboard(Long id, String isFolder) {
      if(!StringUtils.isBlank(isFolder) && !Objects.isNull(id)) {
      	if(Boolean.valueOf(isFolder)) {
      		LOG.info("Getting all dashboards for a given collector. Collector id : "+id);
      		List<CatalogDetail> catList = getCatalog(id);
      		return catList;
      	}else {
      		LOG.info("Getting a dashboard for the given dashboard id : "+id);
      		Dashboard d = getDashboard(id); 
      		CatalogDetail cd = new CatalogDetail();
  			cd.setId(d.getId());
  			cd.setTitle(d.getName());
  			cd.setDescription(d.getDescription());
  			cd.setDashboardJson(new String(d.getDashboard()));
  			List<CatalogDetail> list = new ArrayList<>();
      		list.add(cd);
      		return list;
      	}
      }
      LOG.info("Request to get all dashboards");
      List<Dashboard> dashList = getAllDashboards();
      List<CatalogDetail> catList = new ArrayList<>();
      for(Dashboard d: dashList) {
			CatalogDetail cd = new CatalogDetail();
			cd.setId(d.getId());
			cd.setTitle(d.getName());
			cd.setDescription(d.getDescription());
			cd.setDashboardJson(new String(d.getDashboard()));
			catList.add(cd);
      }
      return catList;
	}
	
	private List<CatalogDetail> getCatalog(Long collectorId) {
		LOG.info("Start service getCatalog");
		String query = "select c from Collector c where c.id = :collectorId";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("collectorId", collectorId).getSingleResult();
		
		Catalog catalog = new Catalog();
		catalog.setId(collector.getId());
		catalog.setCatalogName(collector.getName());
		catalog.setType(collector.getType());
		catalog.setCatalogDescription(collector.getDescription());
		
		String queryDs = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId";
		List<Dashboard> dashboardList = entityManager.createQuery(queryDs, Dashboard.class).setParameter("collectorId", collector.getId()).getResultList();
		
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
		LOG.info("End service getCatalog");
		return catalogDetailList;
	}
	
	private synchronized Long getMax() {
		LOG.info("Start service getMax");
		String query = "select max(c.id) from Dashboard c ";
		Long maxId = entityManager.createQuery(query, Long.class).getSingleResult();
		LOG.info("Max Id: "+maxId);
		LOG.info("End service getMax");
		return maxId;
	}
}
