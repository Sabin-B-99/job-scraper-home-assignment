package com.homeassignment.jobscraper.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

//TODO: Handle IOException better
//TODO: Change url from String object to URL objects
public class JobScraper {

    private final String keyword;

    private final String rootUrl = "https://www.merojob.com";

    private String searchUrl;

    private int resultPageBeingScraped;


    public JobScraper(String keyword) {
        this.keyword = keyword;
    }

    public void scrapeMeroJobs(){
        try {
            Document initialResponseDocument = searchKeyWord();
            int noOfResultPages = getNumberOfPages(initialResponseDocument);
            List<String> searchPageLinks = getJobDetailsPageLinkFromListingPage(initialResponseDocument);
            scrapeJobDetails(searchPageLinks);
            for (int i = 0; i < noOfResultPages; i++) {
                //TODO: Implement later
                searchPageLinks= getJobDetailsPageLinkFromListingPage(i);
                scrapeJobDetails(searchPageLinks);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private Document searchKeyWord() throws IOException {
        searchUrl = rootUrl + "/search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return makeConnectionAndGetResponse(searchUrl);
    }

    private Document makeConnectionAndGetResponse(String url) throws IOException{
        Connection.Response response = Jsoup.connect(url)
                .method(org.jsoup.Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        return response.parse();
    }


    //TODO: Can i make use of optional here??
    //TODO: Check .matcher documentation to see what it returns if no match is found
    private int getNumberOfPages(Document initalSearchResponseDocument){
        String jobCount = initalSearchResponseDocument.select("#job-count").first().text();
        List<String> matchResult = Pattern.compile("\\d+")
                .matcher(jobCount)
                .results()
                .map(MatchResult::group)
                .toList();

        if(matchResult.isEmpty()){
           return -1;
        }

        int resultPerPage = Integer.parseInt(matchResult.get(1));
        int totalNoOfResult = Integer.parseInt(matchResult.get(2));
        return Math.ceilDiv(totalNoOfResult, resultPerPage);
    }

    private List<String> getJobDetailsPageLinkFromListingPage(Document responseDocument){
        Elements detailPageLinks = responseDocument.select("h1[itemprop=title] a");
        List<String> links = new ArrayList<>();
        for (Element e: detailPageLinks){
            String link = e.attr("href");
            links.add(link);
        }
        return links;
    }


    private List<String> getJobDetailsPageLinkFromListingPage(int pageNo) throws IOException{
        searchUrl = rootUrl + "/search?q="
                + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                + "&page=" + pageNo;
        return getJobDetailsPageLinkFromListingPage(makeConnectionAndGetResponse(searchUrl));
    }

    private void scrapeJobDetails(List<String> detailPageLinks) throws IOException{
        for (String link : detailPageLinks){
            Document detailPageResponse = makeConnectionAndGetResponse(rootUrl+link);

        }
    }
}
