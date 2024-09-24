package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.scraper.JobScraper;
import org.springframework.stereotype.Service;

@Service
public class JobScraperServiceImpl implements JobScraperService{

    private final JobScraper scraper;

    public JobScraperServiceImpl(JobScraper scraper) {
        this.scraper = scraper;
    }
    @Override
    public void scrapeJob(String keyword) {
        scraper.scrapeMeroJobs(keyword);
    }
}
