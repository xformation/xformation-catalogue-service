package com.synectiks.process.server.xformation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Library.
 */
@Entity
@Table(name = "library")
public class Library implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "app_name")
    private String appName;

    @Size(max = 2000)
    @Column(name = "virtual_path", length = 2000)
    private String virtualPath;

    @Column(name = "data_source")
    private String dataSource;

    @ManyToOne
    @JsonIgnoreProperties(value = "libraries", allowSetters = true)
    private Collector collector;

    @ManyToOne
    @JsonIgnoreProperties(value = "libraries", allowSetters = true)
    private Folder folder;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public Library appName(String appName) {
        this.appName = appName;
        return this;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public Library virtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
        return this;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public String getDataSource() {
        return dataSource;
    }

    public Library dataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Collector getCollector() {
        return collector;
    }

    public Library collector(Collector collector) {
        this.collector = collector;
        return this;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Folder getFolder() {
        return folder;
    }

    public Library folder(Folder folder) {
        this.folder = folder;
        return this;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Library)) {
            return false;
        }
        return id != null && id.equals(((Library) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Library{" +
            "id=" + getId() +
            ", appName='" + getAppName() + "'" +
            ", virtualPath='" + getVirtualPath() + "'" +
            ", dataSource='" + getDataSource() + "'" +
            ", Collector='" + getCollector() + "'" +
            ", Folder='" + getFolder() + "'" +
            "}";
    }
}
