package com.homeassignment.jobscraper.dtos;

import com.homeassignment.jobscraper.entities.Jobs;

import java.util.List;

public class JobsResponse {
    private List<Jobs> responseContents;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;


    public JobsResponse() {
    }

    public List<Jobs> getResponseContents() {
        return responseContents;
    }

    public void setResponseContents(List<Jobs> responseContents) {
        this.responseContents = responseContents;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
