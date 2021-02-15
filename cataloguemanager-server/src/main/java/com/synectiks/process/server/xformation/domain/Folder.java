package com.synectiks.process.server.xformation.domain;


import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A Folder.
 */
@Entity
@Table(name = "folder")
public class Folder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "is_opened")
    private Boolean isOpened;

    @Column(name = "is_checked")
    private Boolean isChecked;

    @Column(name = "is_folder")
    private Boolean isFolder;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Instant createdOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_on")
    private Instant updatedOn;

    @OneToMany(mappedBy = "collector", fetch = FetchType.EAGER)
    private List<Library> collector;
    
    @OneToMany(mappedBy = "folder", fetch = FetchType.EAGER)
    private List<Library> folder;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Folder title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParentId() {
        return parentId;
    }

    public Folder parentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean isIsOpened() {
        return isOpened;
    }

    public Folder isOpened(Boolean isOpened) {
        this.isOpened = isOpened;
        return this;
    }

    public void setIsOpened(Boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Boolean isIsChecked() {
        return isChecked;
    }

    public Folder isChecked(Boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Boolean isIsFolder() {
        return isFolder;
    }

    public Folder isFolder(Boolean isFolder) {
        this.isFolder = isFolder;
        return this;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Folder createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public Folder createdOn(Instant createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Folder updatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public Folder updatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Folder)) {
            return false;
        }
        return id != null && id.equals(((Folder) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Folder{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", parentId=" + getParentId() +
            ", isOpened='" + isIsOpened() + "'" +
            ", isChecked='" + isIsChecked() + "'" +
            ", isFolder='" + isIsFolder() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedOn='" + getUpdatedOn() + "'" +
            "}";
    }
}
