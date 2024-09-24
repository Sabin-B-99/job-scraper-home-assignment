package com.homeassignment.jobscraper.scraper;


import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.exceptions.ScrapingException;
import com.homeassignment.jobscraper.services.JobsService;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

//TODO: Check for duplicate jobs before saving to datatbase
//TODO: Change url from String object to URL objects
@Component
public class MeroJobsScraper {

    private static final String ROOT_URL = "https://www.merojob.com";
    private final JobsService jobsService;

    private int savedJobs;
    private int totalJobsFound;


    public MeroJobsScraper(JobsService jobsService) {
        this.jobsService = jobsService;
        this.savedJobs = 0;
    }

    public String scrapeMeroJobs(String keyword){
        if(keyword.isBlank()){
           return scrapeAllJobs();
        }else {
          return scrapeJobsByKeyword(keyword);
        }
    }

    private String scrapeAllJobs(){
        Document initialResponseDocument = searchAll();
        int noOfResultPages = getNumberOfPages(initialResponseDocument);
        List<String> searchPageLinks = getJobDetailsPageLinkFromListingPage(initialResponseDocument);
        scrapeJobDetailsAndSaveToDB(searchPageLinks);
        for (int i = 2; i <= noOfResultPages; i++) {
            searchPageLinks= getJobDetailsPageLinkFromListingPage(i);
            scrapeJobDetailsAndSaveToDB(searchPageLinks);
        }
        return "Total jobs found: " + totalJobsFound + " | Saved Jobs: " + savedJobs;
    }

    private String scrapeJobsByKeyword(String keyword){
        Document initialResponseDocument = searchKeyWord(keyword);
        int noOfResultPages = getNumberOfPages(initialResponseDocument);
        List<String> searchPageLinks = getJobDetailsPageLinkFromListingPage(initialResponseDocument);
        scrapeJobDetailsAndSaveToDB(searchPageLinks);
        for (int i = 2; i <= noOfResultPages; i++) {
            searchPageLinks= getJobDetailsPageLinkFromListingPage(keyword, i);
            scrapeJobDetailsAndSaveToDB(searchPageLinks);
        }
        return "Total jobs found: " + totalJobsFound + " | Saved Jobs: " + savedJobs;
    }

    private Document searchKeyWord(String keyword){
        String searchUrl = ROOT_URL + "/search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return makeConnectionAndGetResponse(searchUrl);
    }

    private Document searchAll(){
        String searchUrl = ROOT_URL + "/search/";
        return makeConnectionAndGetResponse(searchUrl);
    }

    private Document makeConnectionAndGetResponse(String url){
        try{
            Connection.Response response = Jsoup.connect(url)
                    .method(org.jsoup.Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();
            return response.parse();
        }catch (IOException e){
            throw new ScrapingException("Cannot connect to " + url, HttpStatus.BAD_GATEWAY, LocalDateTime.now());
        }
    }



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
           throw new ScrapingException("There are no jobs to scrape. Search result - 0", HttpStatus.NOT_FOUND, LocalDateTime.now());
        }

        int resultPerPage = Integer.parseInt(matchResult.get(1));
        this.totalJobsFound = Integer.parseInt(matchResult.get(2));
        return Math.ceilDiv(totalJobsFound, resultPerPage);
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


    private List<String> getJobDetailsPageLinkFromListingPage(String keyword, int pageNo){
        String searchUrl = ROOT_URL + "/search?q="
                + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                + "&page=" + pageNo;
        return getJobDetailsPageLinkFromListingPage(makeConnectionAndGetResponse(searchUrl));
    }

    private List<String> getJobDetailsPageLinkFromListingPage(int pageNo) {
        String searchUrl = ROOT_URL + "/search/"
                + "?page=" + pageNo;
        return getJobDetailsPageLinkFromListingPage(makeConnectionAndGetResponse(searchUrl));
    }

    private void scrapeJobDetailsAndSaveToDB(List<String> detailPageLinks){
        for (String link : detailPageLinks){
            String detailsUrl = ROOT_URL + link;
            System.out.println("[Scraping Detail] " + detailsUrl);
            Document detailPageResponse = makeConnectionAndGetResponse(detailsUrl);
            Jobs job = new Jobs();
            job.setJobTitle(scrapeJobTitle(detailPageResponse));
            job.setCompanyName(scrapeCompanyName(detailPageResponse));
            job.setLocation(scrapeJobLocation(detailPageResponse));
            job.setJobDescription(scrapeJobDescription(detailPageResponse));
            job.setJobInformation(scrapeJobInformation(detailPageResponse));
            job.setJobDetailPageLink(detailsUrl);

            jobsService.saveJob(job);
            savedJobs++;
            System.out.println("[Data Saved] " + detailsUrl);
            System.out.println("[Saved / Total] " + savedJobs + "/" + totalJobsFound);
        }
    }

    private String scrapeCompanyName(Document detailsPageResponseDocument){
        Element nameElem = detailsPageResponseDocument.selectFirst("span[itemprop=name]");
        if(nameElem == null) nameElem = detailsPageResponseDocument.selectFirst("p[itemprop=name]");
        return nameElem.text();
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

        Element descriptionPara = detailsPageResponseDocument
                .selectFirst("div[itemprop=description] > p");

        String description = "\n";
        if(descriptionPara != null) description = descriptionPara.text();

        jobDescriptionBuilder.append(description)
                .append("\n")
                .append("Responsibilities")
                .append("\n");

        Elements responsibilities =  detailsPageResponseDocument.select("div[itemprop=description] > ul > li");
        if(responsibilities == null) {
            responsibilities = detailsPageResponseDocument.select("div[itemprop=description] > p");
            jobDescriptionBuilder.setLength(0);
            jobDescriptionBuilder.append("Job Description").append("\n");
        }

        for (Element responsibility: responsibilities){
            jobDescriptionBuilder.append(responsibility.text()).append("\n");
        }
        return jobDescriptionBuilder.toString();
    }

    private String scrapeJobLocation(Document detailsPageResponseDocument){
        String jobInfo = scrapeJobInformation(detailsPageResponseDocument);
        JSONObject jsonObject = new JSONObject(jobInfo);
        String location = null;
        try {
          location =  jsonObject.getString("Job Location");
        }catch (JSONException e){
            location = "N/A";
        }
        return location;
    }

}
