package com.homeassignment.jobscraper.controller.scraper;

import com.homeassignment.jobscraper.services.JobScraperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/scraper")
public class JobScraperController {

    private final JobScraperService jobScraperService;

    public JobScraperController(JobScraperService jobScraperService) {
        this.jobScraperService = jobScraperService;
    }

    @GetMapping
    public void scrape(@RequestParam(value = "keyword", defaultValue = "") String keyword){
        jobScraperService.scrapeJob(keyword);
    }
}
