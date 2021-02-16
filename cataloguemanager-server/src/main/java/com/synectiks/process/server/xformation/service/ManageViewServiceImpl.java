package com.synectiks.process.server.xformation.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.ManageView;
import com.synectiks.process.server.xformation.domain.ManageViewDetail;

public class ManageViewServiceImpl implements ManageViewService {

	private static final Logger LOG = LoggerFactory.getLogger(ManageViewServiceImpl.class);
	private EntityManager entityManager = null;

	@Inject
    public ManageViewServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
    public ManageView addView(String viewName,String viewJson,String description,String type,String status,String userName) {
    	LOG.info("Start service addView");
        
        ManageView view = new ManageView();
        view.setName(viewName);
        view.setViewData(viewJson.getBytes());
        view.setDescription(description);
        view.setType(type);
        view.setStatus(status);
        
        if(!StringUtils.isBlank(userName)) {
        	view.setCreatedBy(userName);
        	view.setUpdatedBy(userName);
    	}else {
    		view.setCreatedBy("Admin");
    		view.setUpdatedBy("Admin");
    	}
        
    	Instant now = Instant.now();
    	view.setCreatedOn(now);
    	view.setUpdatedOn(now);
        
    	entityManager.persist(view);
    	entityManager.refresh(view);
        LOG.debug("View added in database");
        LOG.info("End service addView");
        return view;
    }
    
	@Override
    public ManageView updateView(Long id,String viewName,String viewJson, String description,String type,String status,String userName){
    	LOG.info("Start service updateView");
    	ManageView view = getManageView(id);
        if(view == null) {
        	LOG.warn("View not found. Returning null");
        	return null;
        }
        if(!StringUtils.isBlank(viewName)) {
        	view.setName(viewName);
        }
        if(!StringUtils.isBlank(viewJson)) {
        	view.setViewData(viewJson.getBytes());
        }
        if(!StringUtils.isBlank(description)) {
        	view.setDescription(description);
        }
        if(!StringUtils.isBlank(type)) {
        	view.setType(type);
        }
        if(!StringUtils.isBlank(type)) {
        	view.setStatus(status);
        }
        
        if(!StringUtils.isBlank(userName)) {
        	view.setUpdatedBy(userName);
    	}else {
    		view.setUpdatedBy("Admin");
    	}
        
    	Instant now = Instant.now();
    	view.setUpdatedOn(now);
        
    	entityManager.merge(view);
    	entityManager.refresh(view);
        LOG.debug("View updated in database");
        LOG.info("End service updateView");
        return view;
    }
    
	@Override
    public void deleteView(Long id) {
    	LOG.info("Start service deleteView. ManageView id: "+id);
    	ManageView mv = getManageView(id);
		if (mv != null) {
			entityManager.remove(mv);
			LOG.debug("ManageView deleted from database");
		} else {
			LOG.warn("No manage view found in database for delete");
		}
		LOG.info("End service deleteView. ManageView id: "+id);
    }

    @Override
    public List<ManageViewDetail> listAllView() {
    	LOG.info("Start service listAllView");
    	String query = "select m from ManageView m ";
    	List<ManageViewDetail> mvList = new ArrayList<>();
    	List<ManageView> list = entityManager.createQuery(query, ManageView.class).getResultList();
    	for(ManageView mv: list) {
    		ManageViewDetail mvd = getManageViewDetail(mv);
    		mvList.add(mvd);
    	}
    	LOG.info("End service listAllView");
        return mvList;
    }
    
    @Override
    public ManageViewDetail getView(Long id) {
    	LOG.info("Start service getView");
    	String query = "select m from ManageView m where m.id =: id";
    	ManageView mv = entityManager.createQuery(query, ManageView.class).setParameter("id", id).getSingleResult();
    	ManageViewDetail mvd = getManageViewDetail(mv);
    	LOG.info("End service getView");
    	return mvd;
    }
    
    @Override
    public ManageView getManageView(Long id) {
    	LOG.info("Start service getManageView");
    	String query = "select m from ManageView m where m.id =: id";
    	ManageView mv = entityManager.createQuery(query, ManageView.class).setParameter("id", id).getSingleResult();
    	LOG.info("End service getManageView");
    	return mv;
    }
    
    private ManageViewDetail getManageViewDetail(ManageView mv) {
		ManageViewDetail mvd = new ManageViewDetail();
		mvd.setId(mv.getId());
		mvd.setName(mv.getName());
		mvd.setDescription(mv.getDescription());
		mvd.setViewJson(new String(mv.getViewData()));
		return mvd;
    }
    
}
