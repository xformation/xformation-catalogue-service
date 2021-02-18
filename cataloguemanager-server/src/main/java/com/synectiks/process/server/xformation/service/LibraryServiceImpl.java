package com.synectiks.process.server.xformation.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.persist.Transactional;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.CatalogDetail;
import com.synectiks.process.server.xformation.domain.Collector;
import com.synectiks.process.server.xformation.domain.Dashboard;
import com.synectiks.process.server.xformation.domain.Folder;
import com.synectiks.process.server.xformation.domain.Library;
import com.synectiks.process.server.xformation.domain.LibraryTree;

public class LibraryServiceImpl implements LibraryService{

	private static final Logger LOG = LoggerFactory.getLogger(LibraryServiceImpl.class);
	private EntityManager entityManager = null;

	@Inject
    public LibraryServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
	@Override
	@Transactional
    public Integer addCollectorToLibrary(ObjectNode obj) {
		LOG.info("Start service addCollectorToLibrary");
        try {
        	String query = "select c from Collector c where c.id =: collectorId";
        	Collector collector = entityManager.createQuery(query, Collector.class).setParameter("collectorId", (obj.get("collectorId").asLong())).getSingleResult();
            String appName = obj.get("appName").asText();
            String dataSource = obj.get("dataSource").asText();
            Iterator<JsonNode> itr = obj.get("folderIdList").elements();
            while (itr.hasNext()) {
    			JsonNode folderId = itr.next();
    			String folderQuery = "select c from Folder c where c.id =: folderId";
    			Folder folder = entityManager.createQuery(folderQuery, Folder.class).setParameter("folderId", (folderId.asLong())).getSingleResult();
    	        Library library = new Library();
    	        library.setCollector(collector);
    	        library.setFolder(folder);
    	        library.setAppName(appName);
    	        library.setDataSource(dataSource);
    	        entityManager.persist(library);
    		}
        }catch(Exception e) {
        	LOG.error("Error adding collector to library : ",e);
        	return HttpStatus.SC_EXPECTATION_FAILED;
        }
        LOG.info("End service addCollectorToLibrary");
        return HttpStatus.SC_OK;
    }
    
	@Override
	@Transactional
    public Library addFolderToLibrary(Long folderId) {
        LOG.info("Start service addFolderToLibrary. Folder id: "+folderId);
        String query = "select f from Folder f where f.id =: folderId";
        Folder folder = entityManager.createQuery(query, Folder.class).setParameter("folderId", folderId).getSingleResult();
        Library library = new Library();
        library.setFolder(folder);
        entityManager.persist(library);
        entityManager.refresh(library);
        LOG.info("End service addFolderToLibrary. Folder id: "+folderId);
        return library;
    }
   
	@Override
	@Transactional
    public Integer removeCollector(Long collectorId, Long folderId) {
    	LOG.info("Start service removeCollector. Collector id: "+collectorId);
        String query = "select f from Folder f where f.id =: folderId";
        Folder folder = entityManager.createQuery(query, Folder.class).setParameter("folderId", folderId).getSingleResult();
        
        String collectorQuery = "select c from Collector c where c.id =: collectorId";
        Collector collector = entityManager.createQuery(collectorQuery, Collector.class).setParameter("collectorId", collectorId).getSingleResult();
        
    	if(folder != null && collector != null) {
    		Library lib = new Library();
    		lib.setFolder(folder);
    		lib.setCollector(collector);
    		entityManager.remove(lib);
    	}
    	LOG.info("End service removeCollector. Collector id: "+collectorId);
    	return HttpStatus.SC_OK;
    }

	@Override
	@Transactional
    public Integer removeFolder(Long folderId) {
    	LOG.info("Start service removeFolder. Folder id: "+folderId);
    	String query = "select f from Folder f where f.id =: folderId";
        Folder folder = entityManager.createQuery(query, Folder.class).setParameter("folderId", folderId).getSingleResult();
    	if(folder != null) {
    		Library lib = new Library();
    		lib.setFolder(folder);
    		entityManager.remove(lib);
    	}
    	return HttpStatus.SC_OK;
    }
    
	@Override
    public List<Library> getAllLibrary() {
    	LOG.info("Start service getAllLibrary");
    	String query = "select l from Library l ";
    	List<Library> list = entityManager.createQuery(query, Library.class).getResultList();
        LOG.info("End service getAllLibrary");
        return list;
    }
    
//    public List<LibraryTree> getLibraryTree() {
//    	return treeService.getLibraryTree();
//    }
    
	@Override
	public List<LibraryTree> getLibraryTree() {
		LOG.info("Start service getLibraryTree");
//        List<Library> libraryList = libraryRepository.findAll(Sort.by(Direction.DESC, "id"));
        
        String query = "select l from Library l order by l.id desc";
    	List<Library> libraryList = entityManager.createQuery(query, Library.class).getResultList();
    	
        Map<Long, LibraryTree> orgMap = new HashMap<Long, LibraryTree>();  
        Map<Long, LibraryTree> mp = new HashMap<>();
        LocalDateTime datetime = null;
        for(Library library: libraryList) {
        	LibraryTree folderNode = null;
        	if(!orgMap.containsKey(library.getFolder().getId())) {
        		folderNode = new LibraryTree();
        		folderNode.setId(library.getFolder().getId());
        		folderNode.setParentId(library.getFolder().getParentId());
        		folderNode.setIsFolder(true);
        		folderNode.setHasChild(true);
        		folderNode.setName(library.getFolder().getTitle());
        		folderNode.setDescription(library.getFolder().getTitle()+" directory");
        		
        		datetime = LocalDateTime.ofInstant(library.getFolder().getUpdatedOn().toInstant(), ZoneId.systemDefault());
        		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
        		folderNode.setCreatedBy(library.getFolder().getUpdatedBy());
        		folderNode.setLastModified(formatedDate + " by "+ library.getFolder().getUpdatedBy());
        		
        		orgMap.put(library.getFolder().getId(), folderNode);
        	}else {
        		folderNode = orgMap.get(library.getFolder().getId());
        	}
        	Collector col = library.getCollector();
    		LibraryTree collectorNode = new LibraryTree();
    		collectorNode.setId(col.getId());
    		collectorNode.setParentId(folderNode.getId());
    		collectorNode.setName(col.getName());
    		collectorNode.setDescription(col.getDescription());
    		collectorNode.setIsFolder(false);
    		collectorNode.setHasChild(false);
    		
    		datetime = LocalDateTime.ofInstant(col.getUpdatedOn().toInstant(), ZoneId.systemDefault());
    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
    		collectorNode.setCreatedBy(col.getUpdatedBy());
    		collectorNode.setLastModified(formatedDate + " by "+ col.getUpdatedBy());
    		
//    		Dashboard dashboard = new Dashboard();
//    		dashboard.setCollector(col);
//    		List<Dashboard> dsList = dashboardRepository.findAll(Example.of(dashboard));
    		
    		String dashboardQuery = "SELECT d FROM Collector c JOIN c.dashboard d WHERE c.id = :collectorId";
    		List<Dashboard> dsList = entityManager.createQuery(dashboardQuery, Dashboard.class).setParameter("collectorId", col.getId()).getResultList();
    		
    		for(Dashboard d: dsList) {
    			CatalogDetail cd = new CatalogDetail();
    			cd.setId(d.getId());
    			cd.setTitle(d.getName());
    			cd.setDescription(d.getDescription());
    			cd.setDashboardJson(new String(d.getDashboard()));
    			datetime = LocalDateTime.ofInstant(d.getUpdatedOn().toInstant(), ZoneId.systemDefault());
        		formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
        		cd.setCreatedBy(d.getUpdatedBy());
        		cd.setLastModified(formatedDate + " by "+ d.getUpdatedBy());
        		
    			collectorNode.getDashboardList().add(cd);
    		}
    		folderNode.getItems().add(collectorNode);
        }
        
        List<LibraryTree> parentList = new ArrayList<>();
        
        for(LibraryTree lt : orgMap.values()) {
        	getSubLibraryTree(lt, parentList, mp);
        }
        
        List<LibraryTree> reList = new ArrayList<>();
        for(LibraryTree lt: mp.values()) {
        	LOG.debug("Parent id : "+lt.getParentId()+", Id: "+lt.getId()+", name : "+lt.getName());
        	if(!Objects.isNull(lt.getParentId())) {
        		LibraryTree parentObj = mp.get(lt.getParentId());
        		boolean isFound = false;
        		for(LibraryTree childObj: parentObj.getItems()) {
        			if(lt.getId().compareTo(childObj.getId()) == 0 ) {
        				isFound = true;
        			}
        		}
        		if(!isFound) {
        			parentObj.getItems().add(lt);
        		}
        	}else {
        		reList.add(lt);
        	}
        }
        
        LibraryTree topNode = new LibraryTree();
        topNode.setName("Library");
        topNode.setIsFolder(true);

        for(LibraryTree lt: reList) {
        	topNode.getItems().add(lt);
        }
        
        List<LibraryTree> finalList = new ArrayList<>();
        finalList.add(topNode);
        return finalList;
    }
    
//    public List<FolderTree> getFoldersTree() {
//    	List<FolderTree> parentList = new ArrayList<>();
//        logger.debug("Getting folder tree");
//        List<Folder> folderList = folderRepository.findAll(Sort.by(Direction.DESC, "id"));
//        LocalDateTime datetime = null;
//        for(Folder f: folderList) {
//        	boolean hasChild = hasChildren(f);
//            FolderTree node = new FolderTree();
//        	node.setHasChild(hasChild);
//        	if(Objects.isNull(f.getParentId())) {
//        		BeanUtils.copyProperties(f, node);
//        		datetime = LocalDateTime.ofInstant(f.getUpdatedOn(), ZoneId.systemDefault());
//        		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//        		node.setCreatedBy(f.getUpdatedBy());
//        		node.setLastModified(formatedDate + " by "+ f.getUpdatedBy());
//        		parentList.add(node);
//        	}
//        }
//        getTree(parentList);
//        return parentList;
//    }
//    
//    private void getTree(List<FolderTree> parentList) {
//    	for(FolderTree ft: parentList) {
//    		if(ft.getHasChild()) {
//    			List<FolderTree> subList = getSubFolderList(ft.getId());
//    			for(FolderTree cft: subList) {
//    				Folder f = new Folder();
//    				BeanUtils.copyProperties(cft, f);
//    				boolean hasChild = hasChildren(f);
//    				cft.setHasChild(hasChild);
//    			}
//    			ft.setSubData(subList);
//    			getTree(subList);
//    		}
//    	}
//    }
//    
//    private boolean hasChildren(Folder parent) {
//		List<FolderTree> list = getSubFolderList(parent.getId());
//		if(list.size() > 0) {
//			return true;
//		}
//        return false;
//    }
//    
//    private List<FolderTree> getSubFolderList(Long parentId){
//    	Folder f = new Folder();
//    	f.setParentId(parentId);
//    	List<Folder> listF = this.folderRepository.findAll(Example.of(f), Sort.by(Direction.ASC, "title"));
//    	List<FolderTree> childList = new ArrayList<>();
//    	LocalDateTime datetime = null;
//    	for(Folder fl: listF) {
//    		FolderTree node = new FolderTree();
//    		BeanUtils.copyProperties(fl, node);
//    		datetime = LocalDateTime.ofInstant(fl.getUpdatedOn(), ZoneId.systemDefault());
//    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//    		node.setCreatedBy(fl.getUpdatedBy());
//    		node.setLastModified(formatedDate + " by "+ fl.getUpdatedBy());
//    		childList.add(node);
//    	}
//    	return childList;
//    }
    
    private void getSubLibraryTree(LibraryTree childNode, List<LibraryTree> parentList, Map<Long, LibraryTree> mp) {
    	mp.put(childNode.getId(), childNode);
    	LocalDateTime datetime = null;
    	if(!Objects.isNull(childNode.getParentId())) {
//    		Folder parentFolder = folderRepository.findById(childNode.getParentId()).get();
    		String query = "select f from Folder f where f.parentId =:parentId ";
    		Folder parentFolder = entityManager.createQuery(query, Folder.class).setParameter("parentId", childNode.getParentId()).getSingleResult();
    		
    		LibraryTree parentNode = new LibraryTree();
    		
    		parentNode.setId(parentFolder.getId());
    		parentNode.setParentId(parentFolder.getParentId());
    		parentNode.setIsFolder(true);
    		parentNode.setHasChild(true);
    		parentNode.setName(parentFolder.getTitle());
    		parentNode.setDescription(parentFolder.getTitle()+" directory");
    		
    		datetime = LocalDateTime.ofInstant(parentFolder.getUpdatedOn().toInstant(), ZoneId.systemDefault());
    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
    		parentNode.setCreatedBy(parentFolder.getUpdatedBy());
    		parentNode.setLastModified(formatedDate + " by "+ parentFolder.getUpdatedBy());
    		
    		LOG.debug("Adding child to the parent");
    		parentNode.getItems().add(childNode);
    		
    		getSubLibraryTree(parentNode, parentList, mp);
    		
    	}else {
    		parentList.add(childNode);
    	}
    }
    
}
