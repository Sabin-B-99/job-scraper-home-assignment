package com.homeassignment.jobscraper.dtos;

import com.homeassignment.jobscraper.entities.Jobs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobsDtoTest {

    private Jobs jobs;

    @BeforeEach
    void setUp() {
        jobs = new Jobs();
        jobs.setId(1);
        jobs.setJobTitle("Java Developer");
        jobs.setCompanyName("XYZ");
        jobs.setLocation("Kathmandu");
        jobs.setJobInformation("Build Java applications");
        jobs.setJobDescription("Experience level: 0");
        jobs.setJobDetailPageLink("www.example.com");
        jobs.setDuplicateCheckHash("abcedf1234");
    }


    @Test
    public void fromJobsTest(){
        JobsDto jobsDto = JobsDto.fromJobs(jobs);
        assertEquals(jobs.getId(), jobsDto.getId());
        assertEquals(jobs.getJobTitle(), jobsDto.getJobTitle());
        assertEquals(jobs.getCompanyName(), jobsDto.getCompanyName());
        assertEquals(jobs.getLocation(), jobsDto.getLocation());
        assertEquals(jobs.getJobInformation(), jobsDto.getJobInformation());
        assertEquals(jobs.getJobDescription(), jobsDto.getJobDescription());
        assertEquals(jobs.getJobDetailPageLink(), jobsDto.getJobDetailPageLink());
        assertNotNull(jobsDto.getLocation());
        assertNotNull(jobsDto.getCompanyName());
    }

}