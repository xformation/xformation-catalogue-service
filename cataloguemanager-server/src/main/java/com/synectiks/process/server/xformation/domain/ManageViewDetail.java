package com.synectiks.process.server.xformation.domain;


import java.io.Serializable;
import java.time.Instant;


public class ManageViewDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private byte[] viewData;
    private String viewDataContentType;
    private String description;
    private String type;
    private String status;
    private String createdBy;
    private Instant createdOn;
    private String updatedBy;
    private Instant updatedOn;
    private String viewJson;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ManageViewDetail name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getViewData() {
        return viewData;
    }

    public ManageViewDetail viewData(byte[] viewData) {
        this.viewData = viewData;
        return this;
    }

    public void setViewData(byte[] viewData) {
        this.viewData = viewData;
    }

    public String getViewDataContentType() {
        return viewDataContentType;
    }

    public ManageViewDetail viewDataContentType(String viewDataContentType) {
        this.viewDataContentType = viewDataContentType;
        return this;
    }

    public void setViewDataContentType(String viewDataContentType) {
        this.viewDataContentType = viewDataContentType;
    }

    public String getDescription() {
        return description;
    }

    public ManageViewDetail description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public ManageViewDetail type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public ManageViewDetail status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public ManageViewDetail createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public ManageViewDetail createdOn(Instant createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public ManageViewDetail updatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public ManageViewDetail updatedOn(Instant updatedOn) {
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
        if (!(o instanceof ManageViewDetail)) {
            return false;
        }
        return id != null && id.equals(((ManageViewDetail) o).id);
    }

    @Override
    public int hashCode() {
        return 41;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManageView{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", viewData='" + getViewData() + "'" +
            ", viewDataContentType='" + getViewDataContentType() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedOn='" + getUpdatedOn() + "'" +
            "}";
    }

	public String getViewJson() {
		return viewJson;
	}

	public void setViewJson(String viewJson) {
		this.viewJson = viewJson;
	}
}
