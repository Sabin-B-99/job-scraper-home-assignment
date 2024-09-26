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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Component
public class MeroJobsScraper {

    private static final String ROOT_URL = "https://www.merojob.com";
    private final JobsService jobsService;

    private int savedJobs;
    private int totalJobsFound;
    private int duplicateCount;
    private final Logger logger = LoggerFactory.getLogger(MeroJobsScraper.class);

    public MeroJobsScraper(JobsService jobsService) {
        this.jobsService = jobsService;
    }

    /**
     * Method to start scraper script.
     * @param keyword  job keywords that can be used to scrape jobs. Scrapes all the job if blank
     * @return  String indicating the total number of jobs found, saved and duplicates if any
     * */
    public String scrapeMeroJobs(String keyword){
        totalJobsFound = 0;
        savedJobs = 0;
        duplicateCount = 0;
        if(keyword.isBlank()){
           return scrapeAllJobs();
        }else {
          return scrapeJobsByKeyword(keyword);
        }
    }

    /**
     * Implementation of method to scrape all jobs
     * @return  String indicating the total number of jobs found, saved and duplicates if any
     * */
    private String scrapeAllJobs(){
        Document initialResponseDocument = searchAll();
        int noOfResultPages = getNumberOfPages(initialResponseDocument);
        List<String> searchPageLinks = getJobDetailsPageLinkFromListingPage(initialResponseDocument);
        scrapeJobDetailsAndSaveToDB(searchPageLinks);
        for (int i = 2; i <= noOfResultPages; i++) {
            searchPageLinks= getJobDetailsPageLinkFromListingPage(i);
            scrapeJobDetailsAndSaveToDB(searchPageLinks);
        }
        return "Total jobs found: " + totalJobsFound + " | New  Jobs Saved: " + savedJobs + " | Duplicate jobs: " + duplicateCount;
    }

    /**
     * Implementation of method to job results matching the keyword. This keyword is used to search in the search bar in the website
     * @return  String indicating the total number of jobs found, saved and duplicates if any
     * */
    private String scrapeJobsByKeyword(String keyword){
        Document initialResponseDocument = searchKeyWord(keyword);
        int noOfResultPages = getNumberOfPages(initialResponseDocument);
        List<String> searchPageLinks = getJobDetailsPageLinkFromListingPage(initialResponseDocument);
        scrapeJobDetailsAndSaveToDB(searchPageLinks);
        for (int i = 2; i <= noOfResultPages; i++) {
            searchPageLinks= getJobDetailsPageLinkFromListingPage(keyword, i);
            scrapeJobDetailsAndSaveToDB(searchPageLinks);
        }
        return "Total jobs found: " + totalJobsFound + " | New  Jobs Saved: " + savedJobs + " | Duplicate jobs: " + duplicateCount;
    }

    /**
     * Searches for job on the merojob website with given keyword
     * @param keyword Job keyword to search for on merojob website. Example: java
     * @return Document response returned by the merojob website after search completes
     * */
    private Document searchKeyWord(String keyword){
        String searchUrl = ROOT_URL + "/search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return makeConnectionAndGetResponse(searchUrl);
    }

    /**
     * Searches all job listings on the merojob website
     * @return Document response returned by the merojob website after search completes
     * */
    private Document searchAll(){
        String searchUrl = ROOT_URL + "/search/";
        return makeConnectionAndGetResponse(searchUrl);
    }

    /**
     * Utility method to make http connection request to a given url
     * @param url to make connection request
     * @return Document response returned after successful connection
     * */
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



    /**
     * After the initial search request completes, we need to find the total number of pages in the search result.
     * So, that we can traverse all the page and scrape all the job listing. This method finds the total number of
     * search result pages from the first page request
     * @param initialSearchResponseDocument response document from the first request
     * @return number of search result pages
     * */
    private int getNumberOfPages(Document initialSearchResponseDocument){
        String jobCount = initialSearchResponseDocument
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

    /**
     * Method to extract job details page link form the listing page
     * @param responseDocument listing page document response
     * @return Arraylist of links of all the job listing in current listing page document response
     * */
    private List<String> getJobDetailsPageLinkFromListingPage(Document responseDocument){
        Elements detailPageLinks = responseDocument.select("h1[itemprop=title] a");
        List<String> links = new ArrayList<>();
        for (Element e: detailPageLinks){
            String link = e.attr("href");
            links.add(link);
        }
        return links;
    }

    /**
     * Method to extract job details page link form the listing pageNo searched with certain keyword.
     * Used when initial job search request is made with certain keyword. Ex: java
     * @param keyword job search keyword used to get the listing page response
     * @param pageNo listing page number
     * @return Arraylist of links of all the job listing in current listing page no document response
     * */
    private List<String> getJobDetailsPageLinkFromListingPage(String keyword, int pageNo){
        String searchUrl = ROOT_URL + "/search?q="
                + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                + "&page=" + pageNo;
        return getJobDetailsPageLinkFromListingPage(makeConnectionAndGetResponse(searchUrl));
    }

    /**
     * Method to extract job details page link form the job listing pageNo.
     * Used when initial job search request is made to search all jobs
     * @param pageNo listing page number
     * @return Arraylist of links of all the job listing in current listing page no document response
     * */
    private List<String> getJobDetailsPageLinkFromListingPage(int pageNo) {
        String searchUrl = ROOT_URL + "/search/"
                + "?page=" + pageNo;
        return getJobDetailsPageLinkFromListingPage(makeConnectionAndGetResponse(searchUrl));
    }

    /**
     * Method that requests the job details page document, after links are extracted for listing pages
     * and scrapes all the required information and saves to the database. Also, checks for duplicates
     * before saving.
     * @param detailPageLinks list of links of job details page
     * */
    private void scrapeJobDetailsAndSaveToDB(List<String> detailPageLinks){
        for (String link : detailPageLinks){
            String detailsUrl = ROOT_URL + link;
            logger.info("[Scraping Detail] {}", detailsUrl);
            Document detailPageResponse = makeConnectionAndGetResponse(detailsUrl);
            Jobs job = new Jobs();
            job.setJobTitle(scrapeJobTitle(detailPageResponse));
            job.setCompanyName(scrapeCompanyName(detailPageResponse));
            job.setLocation(scrapeJobLocation(detailPageResponse));
            job.setJobDescription(scrapeJobDescription(detailPageResponse));
            job.setJobInformation(scrapeJobInformation(detailPageResponse));
            job.setJobDetailPageLink(detailsUrl);
            job.setDuplicateCheckHash(hashForDuplicateVerification(job));

            if(!isDuplicate(job)) {
                Jobs savedJob = jobsService.saveJob(job);
                savedJobs++;
                logger.info("[Data Saved for link: {}] || Job Title:  {} ", detailsUrl, savedJob.getJobTitle());
            }else{
                logger.info("[Duplicate Job] {}", detailsUrl);
                duplicateCount++;
            }
            logger.info("[New Jobs Saved / Duplicate Count / Total Count] {}/{}/{}", savedJobs, duplicateCount, totalJobsFound);
        }
    }

    /**
     * Method to check weather a job that has been scraped is duplicate or not.
     * @param jobs Job to check
     * @return true if duplicate, false otherwise
     * */
    private boolean isDuplicate(Jobs jobs){
        Jobs existingJob = jobsService.getDuplicateCheckHashById(jobs.getDuplicateCheckHash());
        if(existingJob == null) return false;
        return existingJob.getJobTitle().equals(jobs.getJobTitle())
                && existingJob.getCompanyName().equals(jobs.getCompanyName())
                && existingJob.getJobDetailPageLink().equals(jobs.getJobDetailPageLink())
                && existingJob.getDuplicateCheckHash().equals(jobs.getDuplicateCheckHash());
    }

    /**
     * Method to generate hash that is later used for duplicate job verification
     * @param jobs Job whose hash is to be generated
     * @return string representation of SHA-256 hash value
     * */
    private String hashForDuplicateVerification(Jobs jobs){
        String stringToHash = jobs.getCompanyName() + jobs.getJobTitle() + jobs.getJobDetailPageLink();
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = sha.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
            return Arrays.toString(encodedHash);
        }catch (NoSuchAlgorithmException e){
            throw new ScrapingException("Could not validate duplicate job", HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
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
