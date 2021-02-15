package com.synectiks.process.server.xformation.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A LibraryTree.
 */
public class LibraryTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Boolean isOpened = Boolean.FALSE;
    private Boolean isChecked = Boolean.FALSE;
    private Boolean isFolder= Boolean.TRUE;
    private List<LibraryTree> items = new ArrayList<>();
    private Boolean hasChild = Boolean.FALSE;
    private String createdBy;
    private Date createdOn;
    private String updatedOn;
    private Date updatedBy;
    private String lastModified;
    private List<CatalogDetail> dashboardList = new ArrayList();
    
	public Boolean getHasChild() {
		return hasChild;
	}

	public void setHasChild(Boolean hasChild) {
		this.hasChild = hasChild;
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

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public LibraryTree parentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean isIsOpened() {
        return isOpened;
    }

    public LibraryTree isOpened(Boolean isOpened) {
        this.isOpened = isOpened;
        return this;
    }

    public void setIsOpened(Boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Boolean isIsChecked() {
        return isChecked;
    }

    public LibraryTree isChecked(Boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Boolean isIsFolder() {
        return isFolder;
    }

    public LibraryTree isFolder(Boolean isFolder) {
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
        if (!(o instanceof LibraryTree)) {
            return false;
        }
        return id != null && id.equals(((LibraryTree) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Date getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Date updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public List<LibraryTree> getItems() {
		return items;
	}

	public void setItems(List<LibraryTree> items) {
		this.items = items;
	}

	public List<CatalogDetail> getDashboardList() {
		return dashboardList;
	}

	public void setDashboardList(List<CatalogDetail> dashboardList) {
		this.dashboardList = dashboardList;
	}
    
	
    
}
