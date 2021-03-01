package com.synectiks.process.server.xformation.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * A Library.
 */
@Entity
@Table(name = "library")
public class Library implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(sequenceName = "sequence_generator", allocationSize = 1, name = "seq_gen" )
    private Long id;

    @Column(name = "app_name")
    private String appName;

    @Size(max = 2000)
    @Column(name = "virtual_path", length = 2000)
    private String virtualPath;

    @Column(name = "data_source")
    private String dataSource;

    @ManyToOne
    @JoinColumn(name = "collector_id")
    @JsonBackReference
    private Collector collector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "folder_id")
    @JsonBackReference
    private Folder folder;

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
