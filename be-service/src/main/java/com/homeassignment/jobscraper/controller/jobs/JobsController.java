package com.homeassignment.jobscraper.controller.jobs;

import com.homeassignment.jobscraper.dtos.JobsDto;
import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.services.JobsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/jobs")
public class JobsController {
    private final JobsService jobsService;

    public JobsController(JobsService jobsService) {
        this.jobsService = jobsService;
    }

    @GetMapping
    private ResponseEntity<JobsResponse> getJobByKeyword(
            @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = ""+Integer.MAX_VALUE, required = false) int pageSize){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jobsService.getAllJobs(keyword, pageNo, pageSize));
    }

    @GetMapping("{id}")
    private ResponseEntity<JobsDto> getJobById(@PathVariable
                                                   @Min(value = 1, message = "Job id cannot be less than 1")
                                                   @Max(value = Integer.MAX_VALUE, message = "Job id cannot be greater than 2147484647")
                                                   int id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jobsService.getJobById(id));
    }
}
