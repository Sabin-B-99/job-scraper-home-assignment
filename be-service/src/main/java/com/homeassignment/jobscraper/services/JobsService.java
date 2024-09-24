package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.dtos.JobsDto;
import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.entities.Jobs;


public interface JobsService {
    JobsResponse getAllJobs(String keyword, int page, int pageSize);
    JobsDto getJobById(int id);
    void saveJob(Jobs job);
    Jobs getDuplicateCheckHashById(String hashVal);
}
