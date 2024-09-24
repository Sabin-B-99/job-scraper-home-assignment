package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.dtos.JobsDto;
import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.exceptions.JobNotFoundException;
import com.homeassignment.jobscraper.repositories.JobsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if(jobs.getContent().isEmpty()) throw new JobNotFoundException("No jobs found. :(", HttpStatus.NOT_FOUND, LocalDateTime.now());
        jobsResponse.setResponseContents(jobs.getContent()
                .stream()
                .map(JobsDto::fromJobs)
                .toList());
        jobsResponse.setPageNo(jobs.getNumber());
        jobsResponse.setPageSize(jobs.getSize());
        jobsResponse.setTotalElements(jobs.getTotalElements());
        jobsResponse.setTotalPages(jobs.getTotalPages());
        jobsResponse.setLast(jobs.isLast());
        return jobsResponse;
    }

    @Override
    public JobsDto getJobById(int id) {
        return jobsRepository
                .getJobsById(id)
                .map(JobsDto::fromJobs)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " not found. :(", HttpStatus.NOT_FOUND, LocalDateTime.now()));
    }

    @Override
    public void saveJob(Jobs job) {
        jobsRepository.save(job);
    }

    @Override
    public Jobs getDuplicateCheckHashById(String hashVal) {
        return jobsRepository.getDuplicateCheckHashById(hashVal);
    }
}
