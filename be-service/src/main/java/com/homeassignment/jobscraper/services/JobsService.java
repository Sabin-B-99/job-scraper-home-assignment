package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.entities.Jobs;

import java.util.List;

public interface JobsService {
    JobsResponse getAllJobs(String keyword, int page, int pageSize);
    Jobs getJobById(int id);
    void saveJob(Jobs job);
}
