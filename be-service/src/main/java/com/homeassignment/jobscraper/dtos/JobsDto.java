package com.homeassignment.jobscraper.dtos;

import com.homeassignment.jobscraper.entities.Jobs;

public class  JobsDto {
    private int id;
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobDescription;
    private String jobInformation;
    private String jobDetailPageLink;

    public JobsDto() {
    }

    public static JobsDto fromJobs(Jobs jobs){
        JobsDto jobsDto = new JobsDto();
        jobsDto.setId(jobs.getId());
        jobsDto.setCompanyName(jobs.getCompanyName());
        jobsDto.setJobTitle(jobs.getJobTitle());
        jobsDto.setJobDescription(jobs.getJobDescription());
        jobsDto.setLocation(jobs.getLocation());
        jobsDto.setJobInformation(jobs.getJobInformation());
        jobsDto.setJobDetailPageLink(jobs.getJobDetailPageLink());
        return jobsDto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobInformation() {
        return jobInformation;
    }

    public void setJobInformation(String jobInformation) {
        this.jobInformation = jobInformation;
    }

    public String getJobDetailPageLink() {
        return jobDetailPageLink;
    }

    public void setJobDetailPageLink(String jobDetailPageLink) {
        this.jobDetailPageLink = jobDetailPageLink;
    }
}
