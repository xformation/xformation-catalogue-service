package com.synectiks.process.server.xformation.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Folder;
import com.synectiks.process.server.xformation.domain.FolderTree;
import com.synectiks.process.server.xformation.domain.Library;

public class FolderServiceImpl implements FolderService {

	private static final Logger LOG = LoggerFactory.getLogger(FolderServiceImpl.class);
	private EntityManager entityManager = null;

	@Inject
    public FolderServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
	@Override
	@Transactional
    public Folder addFolder(String title, Long parentId) {
        LOG.info("Start service addFolder. Title: "+title+", Parent id: "+parentId);
    	Folder folder = new Folder();
    	Long maxId = getMax();
    	folder.setId(maxId+1);
    	folder.setTitle(title);
		folder.setCreatedBy("Admin");
		folder.setUpdatedBy("Admin");
    	
    	Instant now = Instant.now();
    	Timestamp timestamp = Timestamp.from(now);
    	folder.setCreatedOn(timestamp);
    	folder.setUpdatedOn(timestamp);
        if(!Objects.isNull(parentId)) {
        	String query = "select f from Folder f where f.id = :parentId ";
    		List<Folder> f = entityManager.createQuery(query, Folder.class).setParameter("parentId", parentId).getResultList();
        	if(f != null && f.size() > 0) {
        		folder.setParentId(parentId);
        		entityManager.persist(folder);
        		LOG.info("Folder created: ", folder);
        	}else {
        		LOG.warn("Invalid parent id. Cannot save folder");
        	}
        }else {
        	LOG.info("No parent provided. Its a root folder");
        	entityManager.persist(folder);
        }
        LOG.info("End service addFolder. Title: "+title+", Parent id: "+parentId);
        return folder;
    }

	@Override
    public List<Folder> getAllFolders() {
        LOG.info("Start service getAllFolders");
		String query = "select f from Folder f order by f.id DESC";
		List<Folder> folderList = entityManager.createQuery(query, Folder.class).getResultList();
		LOG.info("End service getAllFolders");
		return folderList;
    }

	@Override
    public List<Library> listCollectorOfFolder(String title) {
        LOG.info("Start service listCollectorOfFolder. Folder title: "+title);
        Folder lf = new Folder();
        lf.setTitle(title);
        String query = "select f from Folder f where f.title = :title ";
		Folder f = entityManager.createQuery(query, Folder.class).setParameter("title", title).getSingleResult();
    	
        if(f == null) {
        	LOG.warn("Folder not found. Returning empty list");
        	return Collections.emptyList();
        }
        
        String libraryQuery = "SELECT l FROM Folder c JOIN c.folder l WHERE c.folderId = :folderId";
		List<Library> list = entityManager.createQuery(libraryQuery, Library.class).setParameter("folderId", f.getId()).getResultList();
		
        LOG.info("End service listCollectorOfFolder. Folder title: "+title);
        return list;
    }
    
	
	@Override
	public List<FolderTree> getFoldersTree() {
		LOG.info("Start service getFoldersTree");
    	List<FolderTree> parentList = new ArrayList<>();
        List<Folder> folderList = getAllFolders();
        LocalDateTime datetime = null;
        for(Folder f: folderList) {
        	boolean hasChild = hasChildren(f);
            FolderTree node = new FolderTree();
        	node.setHasChild(hasChild);
        	if(Objects.isNull(f.getParentId())) {
        		try {
					BeanUtils.copyProperties(node, f);
				} catch (IllegalAccessException | InvocationTargetException e) {
					LOG.warn("BeanUtils copyProperties exception. "+e.getMessage());
				}
//        		Instant in = f.getUpdatedOn();
        		datetime = LocalDateTime.ofInstant(f.getUpdatedOn().toInstant(), ZoneId.systemDefault());
        		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
        		node.setCreatedBy(f.getUpdatedBy());
        		node.setLastModified(formatedDate + " by "+ f.getUpdatedBy());
        		parentList.add(node);
        	}
        }
        getTree(parentList);
        LOG.info("End service getFoldersTree");
        return parentList;
    }
    
    private void getTree(List<FolderTree> parentList) {
    	for(FolderTree ft: parentList) {
    		if(ft.getHasChild()) {
    			List<FolderTree> subList = getSubFolderList(ft.getId());
    			for(FolderTree cft: subList) {
    				Folder f = new Folder();
    				try {
						BeanUtils.copyProperties(f, cft);
					} catch (IllegalAccessException | InvocationTargetException e) {
						LOG.warn("BeanUtils copyProperties exception. "+e.getMessage());
					}
    				boolean hasChild = hasChildren(f);
    				cft.setHasChild(hasChild);
    			}
    			ft.setSubData(subList);
    			getTree(subList);
    		}
    	}
    }
    
    private boolean hasChildren(Folder parent) {
    	LOG.info("Start service hasChildren");
		List<FolderTree> list = getSubFolderList(parent.getId());
		if(list.size() > 0) {
			return true;
		}
		LOG.info("End service hasChildren");
        return false;
    }
    
    private List<FolderTree> getSubFolderList(Long parentId){
    	LOG.info("Start service getSubFolderList");
    	String query = "select f from Folder f where f.parentId = :parentId ";
    	List<Folder> listF = entityManager.createQuery(query, Folder.class).setParameter("parentId", parentId).getResultList();
		
    	List<FolderTree> childList = new ArrayList<>();
    	LocalDateTime datetime = null;
    	for(Folder fl: listF) {
    		FolderTree node = new FolderTree();
    		try {
				BeanUtils.copyProperties(node, fl);
			} catch (IllegalAccessException | InvocationTargetException e) {
				LOG.warn("BeanUtils copyProperties exception. "+e.getMessage());
			}
    		datetime = LocalDateTime.ofInstant(fl.getUpdatedOn().toInstant(), ZoneId.systemDefault());
    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
    		node.setCreatedBy(fl.getUpdatedBy());
    		node.setLastModified(formatedDate + " by "+ fl.getUpdatedBy());
    		LOG.info("Folder tree node : {} ",node);
    		childList.add(node);
    	}
    	LOG.info("End service getSubFolderList");
    	return childList;
    }
    
    private synchronized Long getMax() {
		LOG.info("Start service getMax");
		String query = "select f from Folder f ";
		Long maxId = 0L;
		try {
			List<Folder> list = entityManager.createQuery(query, Folder.class).getResultList();
			if(list == null || (list != null && list.size() == 0)) {
				LOG.info("No record found in table. Returning 0 as max id");
				return maxId;
			}else {
//				maxId = new Long(list.size());
				LOG.info("Record found. Getting max id");
				maxId = entityManager.createQuery("select max(f.id) from Folder f", Long.class).getSingleResult();
			}
		}catch(Exception e) {
			LOG.warn("Record may not be found in the folder table. Returning 0 as max id value. "+e.getMessage());
		}
		LOG.info("Max Id: "+maxId);
		LOG.info("End service getMax");
		return maxId;
	}
    
//    public List<LibraryTree> getLibraryTree() {
//        logger.debug("Request to get library tree");
//        List<Library> libraryList = libraryRepository.findAll(Sort.by(Direction.DESC, "id"));
//        Map<Long, LibraryTree> orgMap = new HashMap<Long, LibraryTree>();  
//        Map<Long, LibraryTree> mp = new HashMap<>();
//        LocalDateTime datetime = null;
//        for(Library library: libraryList) {
//        	LibraryTree folderNode = null;
//        	if(!orgMap.containsKey(library.getFolder().getId())) {
//        		folderNode = new LibraryTree();
//        		folderNode.setId(library.getFolder().getId());
//        		folderNode.setParentId(library.getFolder().getParentId());
//        		folderNode.setIsFolder(true);
//        		folderNode.setHasChild(true);
//        		folderNode.setName(library.getFolder().getTitle());
//        		folderNode.setDescription(library.getFolder().getTitle()+" directory");
//        		
//        		datetime = LocalDateTime.ofInstant(library.getFolder().getUpdatedOn(), ZoneId.systemDefault());
//        		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//        		folderNode.setCreatedBy(library.getFolder().getUpdatedBy());
//        		folderNode.setLastModified(formatedDate + " by "+ library.getFolder().getUpdatedBy());
//        		
//        		orgMap.put(library.getFolder().getId(), folderNode);
//        	}else {
//        		folderNode = orgMap.get(library.getFolder().getId());
//        	}
//        	Collector col = library.getCollector();
//    		LibraryTree collectorNode = new LibraryTree();
//    		collectorNode.setId(col.getId());
//    		collectorNode.setParentId(folderNode.getId());
//    		collectorNode.setName(col.getName());
//    		collectorNode.setDescription(col.getDescription());
//    		collectorNode.setIsFolder(false);
//    		collectorNode.setHasChild(false);
//    		
//    		datetime = LocalDateTime.ofInstant(col.getUpdatedOn(), ZoneId.systemDefault());
//    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//    		collectorNode.setCreatedBy(col.getUpdatedBy());
//    		collectorNode.setLastModified(formatedDate + " by "+ col.getUpdatedBy());
//    		
//    		Dashboard dashboard = new Dashboard();
//    		dashboard.setCollector(col);
//    		List<Dashboard> dsList = dashboardRepository.findAll(Example.of(dashboard));
//    		for(Dashboard d: dsList) {
//    			CatalogDetail cd = new CatalogDetail();
//    			cd.setId(d.getId());
//    			cd.setTitle(d.getName());
//    			cd.setDescription(d.getDescription());
//    			cd.setDashboardJson(new String(d.getDashboard()));
//    			datetime = LocalDateTime.ofInstant(d.getUpdatedOn(), ZoneId.systemDefault());
//        		formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//        		cd.setCreatedBy(d.getUpdatedBy());
//        		cd.setLastModified(formatedDate + " by "+ d.getUpdatedBy());
//        		
//    			collectorNode.getDashboardList().add(cd);
//    		}
//    		folderNode.getItems().add(collectorNode);
//        }
//        
//        List<LibraryTree> parentList = new ArrayList<>();
//        
//        for(LibraryTree lt : orgMap.values()) {
//        	getSubLibraryTree(lt, parentList, mp);
//        }
//        
//        List<LibraryTree> reList = new ArrayList<>();
//        for(LibraryTree lt: mp.values()) {
//        	logger.debug("Parent id : "+lt.getParentId()+", Id: "+lt.getId()+", name : "+lt.getName());
//        	if(!Objects.isNull(lt.getParentId())) {
//        		LibraryTree parentObj = mp.get(lt.getParentId());
//        		boolean isFound = false;
//        		for(LibraryTree childObj: parentObj.getItems()) {
//        			if(lt.getId().compareTo(childObj.getId()) == 0 ) {
//        				isFound = true;
//        			}
//        		}
//        		if(!isFound) {
//        			parentObj.getItems().add(lt);
//        		}
//        	}else {
//        		reList.add(lt);
//        	}
//        }
//        
//        LibraryTree topNode = new LibraryTree();
//        topNode.setName("Library");
//        topNode.setIsFolder(true);
//
//        for(LibraryTree lt: reList) {
//        	topNode.getItems().add(lt);
//        }
//        
//        List<LibraryTree> finalList = new ArrayList<>();
//        finalList.add(topNode);
//        return finalList;
//    }
//    
//    private void getSubLibraryTree(LibraryTree childNode, List<LibraryTree> parentList, Map<Long, LibraryTree> mp) {
//    	mp.put(childNode.getId(), childNode);
//    	LocalDateTime datetime = null;
//    	if(!Objects.isNull(childNode.getParentId())) {
//    		Folder parentFolder = folderRepository.findById(childNode.getParentId()).get();
//    		LibraryTree parentNode = new LibraryTree();
//    		
//    		parentNode.setId(parentFolder.getId());
//    		parentNode.setParentId(parentFolder.getParentId());
//    		parentNode.setIsFolder(true);
//    		parentNode.setHasChild(true);
//    		parentNode.setName(parentFolder.getTitle());
//    		parentNode.setDescription(parentFolder.getTitle()+" directory");
//    		
//    		datetime = LocalDateTime.ofInstant(parentFolder.getUpdatedOn(), ZoneId.systemDefault());
//    		String formatedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(datetime);
//    		parentNode.setCreatedBy(parentFolder.getUpdatedBy());
//    		parentNode.setLastModified(formatedDate + " by "+ parentFolder.getUpdatedBy());
//    		
//    		logger.debug("Adding child to the parent");
//    		parentNode.getItems().add(childNode);
//    		
//    		getSubLibraryTree(parentNode, parentList, mp);
//    		
//    	}else {
//    		parentList.add(childNode);
//    	}
//    }
    
}
