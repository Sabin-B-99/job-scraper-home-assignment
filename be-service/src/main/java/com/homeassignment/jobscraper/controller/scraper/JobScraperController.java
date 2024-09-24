package com.homeassignment.jobscraper.controller.scraper;

import com.homeassignment.jobscraper.services.JobScraperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/scraper")
public class JobScraperController {

    private final JobScraperService jobScraperService;

    public JobScraperController(JobScraperService jobScraperService) {
        this.jobScraperService = jobScraperService;
    }

    @PostMapping
    public ResponseEntity<String> scrape(@RequestParam(value = "keyword", defaultValue = "") String keyword){
        return ResponseEntity
                .status(HttpStatus.OK)
                        .body(jobScraperService.scrapeJob(keyword));
    }
}
