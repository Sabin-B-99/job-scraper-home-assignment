package com.homeassignment.jobscraper.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "job_title", columnDefinition = "VARCHAR(256)", nullable = false)
    private String jobTitle;

    @Column(name = "company_name",  columnDefinition = "VARCHAR(256)", nullable = false)
    private String companyName;

    @Column(name = "location", columnDefinition = "VARCHAR(256)", nullable = false)
    private String location;

    @Column(name = "job_description",columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "job_information", columnDefinition = "TEXT")
    private String jobInformation;

    @Column(name = "job_detail_page_link", columnDefinition = "VARCHAR(256)", nullable = false)
    private String jobDetailPageLink;

    @Column(name = "duplicate_check_hash", columnDefinition = "VARCHAR(256)", nullable = false)
    private String duplicateCheckHash;

    public Jobs() {
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

    public String getDuplicateCheckHash() {
        return duplicateCheckHash;
    }

    public void setDuplicateCheckHash(String duplicateCheckHash) {
        this.duplicateCheckHash = duplicateCheckHash;
    }
}
