package com.synectiks.process.server.xformation.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
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
		String query = "select d from Dashboard d where d.id =: dashboardId";
		Dashboard dashboard = entityManager.createQuery(query, Dashboard.class).setParameter("dashboardId", dashboardId).getSingleResult();
		LOG.info("End service getDashboard. Dashboard id: "+dashboardId);
		return dashboard;
	}
	
	@Override
	public Dashboard createDashboard(Long collectorId, String dashboardName, String dashboardJson, String dashboardDoc) {
		
		LOG.info("Start service createDashboard");
		String query = "select c from Collector c where c.id =: id";
		Collector collector = entityManager.createQuery(query, Collector.class).setParameter("id", collectorId).getSingleResult();
		if(collector == null) {
			LOG.warn("Collector not found. Returning null. Collector Id: "+collectorId);
			return null;
		}
		Dashboard dashboard = new Dashboard();
        dashboard.setCollector(collector);
        dashboard.setName(dashboardName);
        dashboard.setDashboard(dashboardJson.getBytes());
        dashboard.setDescription(dashboardDoc);
//    	dashboard.setCreatedBy(userContext.getUserId());
//		dashboard.setUpdatedBy(userContext.getUserId());
    	Instant now = Instant.now();
    	dashboard.setCreatedOn(now);
    	dashboard.setUpdatedOn(now);
    	entityManager.persist(dashboard);
    	LOG.debug("Dashboard created in database");
    	entityManager.refresh(dashboard);
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
	
//	public List<CatalogDetail> listAllDashboard(Long id, String isFolder) {
//      if(!StringUtils.isBlank(isFolder) && !Objects.isNull(id)) {
//      	logger.info("Request to get dashboards for id : "+id);
//      	if(Boolean.valueOf(isFolder)) {
//      		logger.info("Getting all dashboards of the given collector id : "+id);
//      		Collector col = collectorRepository.findById(id).get();
//      		Dashboard dashboard = new Dashboard();
//      		dashboard.setCollector(col);
//      		List<Dashboard> dashList = dashboardRepository.findAll(Example.of(dashboard));
//      		List<CatalogDetail> catList = new ArrayList<>();
//      		for(Dashboard d: dashList) {
//      			CatalogDetail cd = new CatalogDetail();
//      			cd.setId(d.getId());
//      			cd.setTitle(d.getName());
//      			cd.setDescription(d.getDescription());
//      			cd.setDashboardJson(new String(d.getDashboard()));
//      			catList.add(cd);
//      		}
//      		return catList;
//      	}else {
//      		logger.info("Getting a dashboard of the given id : "+id);
//      		Dashboard d = dashboardRepository.findById(id).get();
//      		CatalogDetail cd = new CatalogDetail();
//  			cd.setId(d.getId());
//  			cd.setTitle(d.getName());
//  			cd.setDescription(d.getDescription());
//  			cd.setDashboardJson(new String(d.getDashboard()));
//  			List<CatalogDetail> list = new ArrayList<>();
//      		list.add(cd);
//      		return list;
//      	}
//      }
//  	logger.info("Request to get all dashboards");
//      List<Dashboard> dashList =  dashboardRepository.findAll(Sort.by(Direction.DESC, "id"));
//      List<CatalogDetail> catList = new ArrayList<>();
//		for(Dashboard d: dashList) {
//			CatalogDetail cd = new CatalogDetail();
//			cd.setId(d.getId());
//			cd.setTitle(d.getName());
//			cd.setDescription(d.getDescription());
//			cd.setDashboardJson(new String(d.getDashboard()));
//			catList.add(cd);
//		}
//		return catList;
//  }
}
