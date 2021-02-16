package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.synectiks.process.server.xformation.domain.ManageView;
import com.synectiks.process.server.xformation.domain.ManageViewDetail;

public interface ManageViewService {

    public ManageView addView(String viewName, String viewJson, String description, String type, String status, String userName);
    public ManageView updateView(Long id,String viewName,String viewJson,String description,String type,String status,String userName);
    public void deleteView(Long id);
    public List<ManageViewDetail> listAllView();
    public ManageViewDetail getView(Long id);
    public ManageView getManageView(Long id);
    
}
