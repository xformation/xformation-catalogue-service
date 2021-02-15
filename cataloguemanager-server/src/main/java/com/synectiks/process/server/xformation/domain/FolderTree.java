package com.synectiks.process.server.xformation.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A FolderTree.
 */
public class FolderTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Long parentId;
    private Boolean isOpened = Boolean.FALSE;
    private Boolean isChecked = Boolean.FALSE;
    private Boolean isFolder= Boolean.TRUE;
    private List<FolderTree> subData = new ArrayList<>();
    private Boolean hasChild = Boolean.FALSE;
    private String createdBy;
    private String lastModified;
    
    public Boolean getHasChild() {
		return hasChild;
	}

	public void setHasChild(Boolean hasChild) {
		this.hasChild = hasChild;
	}

	public List<FolderTree> getSubData() {
		return subData;
	}

	public Boolean getIsOpened() {
		return isOpened;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setSubData(List<FolderTree> subData) {
		this.subData = subData;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public FolderTree title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParentId() {
        return parentId;
    }

    public FolderTree parentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean isIsOpened() {
        return isOpened;
    }

    public FolderTree isOpened(Boolean isOpened) {
        this.isOpened = isOpened;
        return this;
    }

    public void setIsOpened(Boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Boolean isIsChecked() {
        return isChecked;
    }

    public FolderTree isChecked(Boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Boolean isIsFolder() {
        return isFolder;
    }

    public FolderTree isFolder(Boolean isFolder) {
        this.isFolder = isFolder;
        return this;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FolderTree)) {
            return false;
        }
        return id != null && id.equals(((FolderTree) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Folder{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", parentId=" + getParentId() +
            ", isOpened='" + isIsOpened() + "'" +
            ", isChecked='" + isIsChecked() + "'" +
            ", isFolder='" + isIsFolder() + "'" +
            "}";
    }

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
}
