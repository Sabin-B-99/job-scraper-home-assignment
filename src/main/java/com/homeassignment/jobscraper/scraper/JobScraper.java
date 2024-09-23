package com.homeassignment.jobscraper.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class JobScraper implements Scraper {

    @Value("job.website.root")
    private String rootUrl;

    public JobScraper() {
    }

    @Override
    public Document searchKeyWord(String keyword) throws IOException {
        Connection.Response response = Jsoup.connect("https://merojob.com/search/?q=java")
                .method(org.jsoup.Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        return response.parse();
    }
    private void getNumberOfPages(){
    }
    private void getJobDetailsPageLinkFromListingPage(){
    }
    private void scrapeJobDetails(){
    }
}
