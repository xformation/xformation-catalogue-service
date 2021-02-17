package com.synectiks.process.server.xformation.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.Library;
import com.synectiks.process.server.xformation.domain.LibraryTree;
import com.synectiks.process.server.xformation.service.LibraryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(value = "Xformation/Library", description = "Manage all xformation libraries")
@Path("/xformation/library")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryController extends RestResource  {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryController.class);
    private final LibraryService libraryService;
	
	@Inject
	public LibraryController(LibraryService libraryService) {
		this.libraryService = libraryService;
	}
	
    @POST
    @ApiOperation("Add a collector to a library")
    public Integer addCollectorToLibrary(@ApiParam(name = "JSON Body") ObjectNode obj) {
    	LOG.info("Start controller addCollectorToLibrary");
//    	LibraryService ls = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	Integer status = this.libraryService.addCollectorToLibrary(obj);
    	LOG.info("End controller addCollectorToLibrary");
        return status;
    }
    
    @POST
    @Path("/addFolderToLibrary")
    @ApiOperation("Add a folder to a library")
    public Library addFolderToLibrary(@PathParam("parentId") Long folderId) {
    	LOG.info("Start controller addFolderToLibrary");
//    	LibraryService ls = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	Library library = this.libraryService.addFolderToLibrary(folderId);
    	LOG.info("End controller addFolderToLibrary");
    	return library;
    }
   
    @DELETE
    @Path("/removeCollector")
    @ApiOperation("Delete a collector from a library")
    public Integer removeCollector(@PathParam("collectorId") Long collectorId, @PathParam("folderId") Long folderId) throws URISyntaxException {
    	LOG.info("Start controller removeCollector");
//    	LibraryService ls = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	Integer status = this.libraryService.removeCollector(collectorId, folderId);
    	LOG.info("End controller removeCollector");
    	return status;
    }
    
    @DELETE
    @Path("/removeFolder/{folderId}")
    @ApiOperation("Delete a folder from a library")
    public Integer removeFolder(@PathParam("folderId")  Long folderId) {
    	LOG.info("Start controller removeFolder");
//    	LibraryService ls = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	Integer status = this.libraryService.removeFolder(folderId);
    	LOG.info("End controller removeFolder");
    	return status;
    }
    
    @GET
    @ApiOperation("Get all libraries")
    public List<Library> getAllLibrary() {
        LOG.info("Start controller getAllLibrary");
//        LibraryService fs = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	List<Library> list = this.libraryService.getAllLibrary();
    	LOG.info("End controller getAllLibrary");
    	return list;
    }
    
    @GET
    @Path("/listLibraryTree")
    @ApiOperation("Get complete library tree")
    public List<LibraryTree> getLibraryTree() {
    	LOG.info("Start controller getLibraryTree");
//    	LibraryService ls = GuiceInjectorHolder.getInjector().getInstance(LibraryService.class);
    	List<LibraryTree> libList = this.libraryService.getLibraryTree();
    	LOG.info("End controller getLibraryTree");
        return libList;
    }
    
}
