package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.synectiks.process.common.security.UserContext;
import com.synectiks.process.server.xformation.domain.Catalog;

public interface CollectorService {
	public List<Catalog> getAllCollectors();
	public Catalog getCatalog(Long catalogId);
	public void createCatalog(String name, String type, String description);
	public void updateCatalog(Long id, String dataSource);
}
