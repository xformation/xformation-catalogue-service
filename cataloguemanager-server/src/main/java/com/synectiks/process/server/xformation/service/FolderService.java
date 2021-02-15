package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.synectiks.process.server.xformation.domain.Folder;
import com.synectiks.process.server.xformation.domain.FolderTree;
import com.synectiks.process.server.xformation.domain.Library;

public interface FolderService {

	public List<Folder> addFolder(String title, Long parentId, String userName);
    public List<Folder> getAllFolders();
    public List<FolderTree> getFoldersTree();
    public List<Library> listCollectorOfFolder(String title);
}
