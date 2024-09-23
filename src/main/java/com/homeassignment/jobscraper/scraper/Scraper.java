package com.homeassignment.jobscraper.scraper;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public interface Scraper {
    public Document searchKeyWord(String keyword) throws IOException;
}
