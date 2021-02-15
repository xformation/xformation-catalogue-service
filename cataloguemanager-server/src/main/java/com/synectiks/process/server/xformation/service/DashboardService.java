package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Dashboard;

public interface DashboardService {

	public Dashboard createDashboard(Long collectorId, String dashboardName, String dashboardJson, String dashboardDoc);
	public Dashboard getDashboard(Long dashboardId);
	public void deleteDashboard(Long dashboardId);
	public List<Dashboard> listDashboardOfCollector(Long collectorId);
	public List<Dashboard> getAllDashboards();
	public List<CatalogDetail> listAllDashboard(Long id, String isFolder);
}
