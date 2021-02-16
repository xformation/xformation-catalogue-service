package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synectiks.process.server.xformation.domain.Library;
import com.synectiks.process.server.xformation.domain.LibraryTree;

public interface LibraryService {
	public Integer addCollectorToLibrary(ObjectNode obj);
    public Library addFolderToLibrary(Long folderId);
    public Integer removeCollector(Long collectorId, Long folderId);
    public Integer removeFolder(Long folderId);
    public List<Library> getAllLibrary();
    public List<LibraryTree> getLibraryTree();
}

