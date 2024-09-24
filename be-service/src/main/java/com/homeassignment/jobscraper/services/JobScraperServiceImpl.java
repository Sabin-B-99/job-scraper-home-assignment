package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.scraper.MeroJobsScraper;
import org.springframework.stereotype.Service;

@Service
public class JobScraperServiceImpl implements JobScraperService{

    private final MeroJobsScraper scraper;

    public JobScraperServiceImpl(MeroJobsScraper scraper) {
        this.scraper = scraper;
    }
    @Override
    public String scrapeJob(String keyword) {
        return scraper.scrapeMeroJobs(keyword);
    }
}
