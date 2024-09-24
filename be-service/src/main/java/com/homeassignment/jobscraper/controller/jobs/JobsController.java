package com.homeassignment.jobscraper.controller.jobs;

import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.services.JobsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/jobs")
public class JobsController {
    private final JobsService jobsService;

    public JobsController(JobsService jobsService) {
        this.jobsService = jobsService;
    }

    @GetMapping
    private List<Jobs> getJobByKeyword(@RequestParam(value = "keyword", defaultValue = "") String keyword){
        return null;
    }

    @GetMapping("{id}")
    private Jobs getJobById(@PathVariable int id){
        return null;
    }

}
