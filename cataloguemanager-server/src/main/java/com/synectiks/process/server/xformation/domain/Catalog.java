package com.synectiks.process.server.xformation.domain;

import java.util.List;

public class Catalog {
	private Long id;
	private String catalogName;
	private String type;
	private String catalogDescription = "Add To library Preview Dashboard";
	private String catalogImage;
	private List<CatalogDetail> catalogDetail;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCatalogDescription() {
		return catalogDescription;
	}
	public void setCatalogDescription(String catalogDescription) {
		this.catalogDescription = catalogDescription;
	}
	public String getCatalogImage() {
		return catalogImage;
	}
	public void setCatalogImage(String catalogImage) {
		this.catalogImage = catalogImage;
	}
	public List<CatalogDetail> getCatalogDetail() {
		return catalogDetail;
	}
	public void setCatalogDetail(List<CatalogDetail> catalogDetail) {
		this.catalogDetail = catalogDetail;
	}
}
