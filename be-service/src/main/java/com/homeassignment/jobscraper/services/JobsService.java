package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.entities.Jobs;

import java.util.List;

public interface JobsService {
    List<Jobs> getAllJobs();
    Jobs getJobById(int id);
    Jobs saveJob(Jobs job);
}
