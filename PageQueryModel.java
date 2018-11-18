package com.huasisoft.hae.common.page;

public abstract class PageQueryModel {

	//当前页码
	private Integer currentPage = 1;
	//每页最大记录数
	private Integer pageSize = 10;
	
	
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	} 
	
	
}
