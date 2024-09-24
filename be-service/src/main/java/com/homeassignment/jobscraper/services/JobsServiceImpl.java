package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.repositories.JobsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsServiceImpl implements JobsService{

    private final JobsRepository jobsRepository;

    public JobsServiceImpl(JobsRepository jobsRepository) {
        this.jobsRepository = jobsRepository;
    }

    @Override
    public List<Jobs> getAllJobs() {
        return List.of();
    }

    @Override
    public Jobs getJobById(int id) {
        return null;
    }

    @Override
    public Jobs saveJob(Jobs job) {
        return jobsRepository.save(job);
    }
}
