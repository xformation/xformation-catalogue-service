package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.Folder;
import com.synectiks.process.server.xformation.domain.FolderTree;
import com.synectiks.process.server.xformation.domain.Library;
import com.synectiks.process.server.xformation.service.FolderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Xformation/Folder", description = "Manage all xformation folders")
@Path("/xformation/folder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FolderController extends RestResource  {

    private static final Logger LOG = LoggerFactory.getLogger(FolderController.class);
    private final FolderService folderService;
	
	@Inject
	public FolderController(FolderService folderService) {
		this.folderService = folderService;
	}
	
    @POST
    @ApiOperation("Add a folder")
    public List<Folder> addFolder(@ApiParam(name = "title") @PathParam("title") @NotBlank String title,
    		@ApiParam(name = "parentId") @PathParam("parentId") Long parentId,
    		@ApiParam(name = "userName") @PathParam("userName") String userName) {
    	LOG.info("Start controller addFolder");
//    	FolderService fs = GuiceInjectorHolder.getInjector().getInstance(FolderService.class);
    	List<Folder> fsList = this.folderService.addFolder(title, parentId, userName);
    	LOG.info("End controller addFolder");
    	return fsList;
//    	return Response.ok().entity(fsList).build();
    }

//    @GetMapping("/listFolder")
    @GET
    @ApiOperation("Get all folders")
    public List<Folder> getAllFolders() {
    	LOG.info("Start controller getAllFolders");
//    	FolderService fs = GuiceInjectorHolder.getInjector().getInstance(FolderService.class);
    	List<Folder> fsList = this.folderService.getAllFolders();
    	LOG.info("End controller getAllFolders");
    	return fsList;
    }
//
//    
//    @GetMapping("/listFolderTree")
    @GET
    @Path("/listFolderTree")
    @ApiOperation("Get complete folders tree")
    public List<FolderTree> getFoldersTree() {
        LOG.info("Start controller getFoldersTree");
//    	FolderService fs = GuiceInjectorHolder.getInjector().getInstance(FolderService.class);
    	List<FolderTree> fsList = this.folderService.getFoldersTree();
    	LOG.info("End controller getFoldersTree");
        return fsList;
    }
//        
//
//    @GetMapping("/listCollectorOfFolder/{folder}")
    @GET
    @Path("/listCollectorOfFolder/{folder}")
    @ApiOperation("Get complete library tree for a given folder")
    public List<Library> listCollectorOfFolder(@ApiParam(name = "title") @PathParam("title") String title) {
        LOG.info("Start controller listCollectorOfFolder. Folder title: "+title);
//        FolderService fs = GuiceInjectorHolder.getInjector().getInstance(FolderService.class);
        List<Library> list = this.folderService.listCollectorOfFolder(title);
        LOG.info("End controller listCollectorOfFolder. Folder title: "+title);
        return list;
    }
}
