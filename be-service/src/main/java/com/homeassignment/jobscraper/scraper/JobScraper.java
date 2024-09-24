package com.homeassignment.jobscraper.scraper;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.json.JSONObject;
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
//TODO: Required info - job title, company name, location, and job description
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
        String jobCount = initalSearchResponseDocument
                .selectFirst("#job-count")
                .text();
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

    private String scrapeCompanyName(Document detailsPageResponseDocument){
        return detailsPageResponseDocument.selectFirst("span[itemprop=name]").text();
    }

    private String scrapeJobTitle(Document detailsPageResponseDocument){
        return detailsPageResponseDocument.selectFirst("h1[itemprop=title]").text();
    }

    private String scrapeJobInformation(Document detailsPageResopnseDocument){
        Elements tableRows = detailsPageResopnseDocument.select("table > tbody > tr");
        JSONObject jsonObject = new JSONObject();
        for(Element tableRow: tableRows){
            Elements tableData = tableRow.select("td");
            String key =  tableData.get(0).text();
            String value = tableData.get(2).text();
            jsonObject.put(key, value);
        }
        return jsonObject.toString();
    }

    private String scrapeJobDescription(Document detailsPageResponseDocument){
        StringBuilder jobDescriptionBuilder = new StringBuilder();
        jobDescriptionBuilder.append("Job Description").append("\n");

        String description = detailsPageResponseDocument
                .selectFirst("div[itemprop=description] > p")
                .text();

        jobDescriptionBuilder.append(description)
                .append("\n")
                .append("Responsibilities")
                .append("\n");

        Element responsibilities =  detailsPageResponseDocument.selectFirst("div[itemprop=description] > ul");
        Elements responsibilityList = responsibilities.select("li");

        for (Element responsibility: responsibilityList){
            jobDescriptionBuilder.append(responsibility.text()).append("\n");
        }
        return jobDescriptionBuilder.toString();
    }

}
