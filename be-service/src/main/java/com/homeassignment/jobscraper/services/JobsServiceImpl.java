package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.repositories.JobsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsServiceImpl implements JobsService{

    private final JobsRepository jobsRepository;

    public JobsServiceImpl(JobsRepository jobsRepository) {
        this.jobsRepository = jobsRepository;
    }

    @Override
    public JobsResponse getAllJobs(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        if(!keyword.isBlank()){
            return jobsResponse(jobsRepository.getJobsByKeyWord(keyword, pageable));
        }
        return jobsResponse(jobsRepository.getAllJobs(pageable));
    }

    private JobsResponse jobsResponse(Page<Jobs> jobs){
        JobsResponse jobsResponse = new JobsResponse();
        jobsResponse.setResponseContents(jobs.getContent());
        jobsResponse.setPageNo(jobs.getNumber());
        jobsResponse.setPageSize(jobs.getSize());
        jobsResponse.setTotalElements(jobs.getTotalElements());
        jobsResponse.setTotalPages(jobs.getTotalPages());
        jobsResponse.setLast(jobs.isLast());
        return jobsResponse;
    }

    @Override
    public Jobs getJobById(int id) {
        return jobsRepository
                .getJobsById(id)
                .orElseThrow(() -> new RuntimeException("Job with id " + id + " not found"));
    }

    @Override
    public void saveJob(Jobs job) {
        jobsRepository.save(job);
    }
}
