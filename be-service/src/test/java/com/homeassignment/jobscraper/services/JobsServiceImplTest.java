package com.homeassignment.jobscraper.services;

import com.homeassignment.jobscraper.dtos.JobsDto;
import com.homeassignment.jobscraper.dtos.JobsResponse;
import com.homeassignment.jobscraper.entities.Jobs;
import com.homeassignment.jobscraper.exceptions.JobNotFoundException;
import com.homeassignment.jobscraper.repositories.JobsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

class JobsServiceImplTest {

    @InjectMocks
    private JobsServiceImpl jobsService;

    @Mock
    private JobsRepository jobsRepository;

    private Jobs jobs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jobs = new Jobs();
        jobs.setId(1);
        jobs.setJobTitle("Java Developer");
        jobs.setCompanyName("XYZ");
        jobs.setLocation("Kathmandu");
        jobs.setJobInformation("Build Java applications");
        jobs.setJobDescription("Experience level: 0");
        jobs.setJobDetailPageLink("www.example.com");
        jobs.setDuplicateCheckHash("abcedf1234");

    }


    @Test
    public void saveJobTest(){

        Jobs savedJobs = new Jobs();
        savedJobs.setId(1);
        savedJobs.setJobTitle("Java Developer");
        savedJobs.setCompanyName("XYZ");
        savedJobs.setLocation("Kathmandu");
        savedJobs.setJobInformation("Build Java applications");
        savedJobs.setJobDescription("Experience level: 0");
        savedJobs.setJobDetailPageLink("www.example.com");
        savedJobs.setDuplicateCheckHash("abcedf1234");

        Mockito.when(jobsRepository.save(jobs)).thenReturn(savedJobs);

        Jobs actualJobSaved = jobsService.saveJob(jobs);

        assertEquals(savedJobs.getId(), actualJobSaved.getId());
        assertEquals(savedJobs.getJobDescription(), actualJobSaved.getJobDescription());
        assertEquals(savedJobs.getJobTitle(), actualJobSaved.getJobTitle());
        assertEquals(savedJobs.getJobDetailPageLink(), actualJobSaved.getJobDetailPageLink());
        assertEquals(savedJobs.getDuplicateCheckHash(), actualJobSaved.getDuplicateCheckHash());

        Mockito.verify(jobsRepository, Mockito.times(1)).save(jobs);
    }

    @Test
    public void getDuplicateCheckHashByIdTest(){
        Jobs jobToCheck = new Jobs();
        jobToCheck.setId(1);
        jobToCheck.setJobTitle("Java Developer");
        jobToCheck.setCompanyName("XYZ");
        jobToCheck.setLocation("Kathmandu");
        jobToCheck.setJobInformation("Build Java applications");
        jobToCheck.setJobDescription("Experience level: 0");
        jobToCheck.setJobDetailPageLink("www.example.com");
        jobToCheck.setDuplicateCheckHash("abcedf1234");

        Mockito.when(jobsRepository.getDuplicateCheckHashById(jobToCheck.getDuplicateCheckHash()))
                .thenReturn(jobs);

        Jobs savedJob = jobsService.getDuplicateCheckHashById(jobs.getDuplicateCheckHash());

        assertEquals(jobToCheck.getDuplicateCheckHash(), savedJob.getDuplicateCheckHash());
        Mockito.verify(jobsRepository, Mockito.times(1))
                .getDuplicateCheckHashById(jobToCheck.getDuplicateCheckHash());
    }


    @Test
    public void getJobByIdTest(){
        Mockito.when(jobsRepository.getJobsById(jobs.getId()))
                .thenReturn(Optional.of(jobs));

        Jobs expectedJobDto = new Jobs();
        expectedJobDto.setId(1);
        expectedJobDto.setJobTitle("Java Developer");
        expectedJobDto.setCompanyName("XYZ");
        expectedJobDto.setLocation("Kathmandu");
        expectedJobDto.setJobInformation("Build Java applications");
        expectedJobDto.setJobDescription("Experience level: 0");
        expectedJobDto.setJobDetailPageLink("www.example.com");

        JobsDto returnedDto = jobsService.getJobById(jobs.getId());

        assertEquals(expectedJobDto.getId(), returnedDto.getId());
        assertEquals(expectedJobDto.getJobTitle(), returnedDto.getJobTitle());
        assertEquals(expectedJobDto.getJobInformation(), returnedDto.getJobInformation());
        assertEquals(expectedJobDto.getJobDetailPageLink(), returnedDto.getJobDetailPageLink());

        JobNotFoundException jobNotFoundException =  assertThrows(JobNotFoundException.class,
                () -> jobsService.getJobById(12));

        assertEquals("Job with id " + 12 + " not found. :(",  jobNotFoundException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, jobNotFoundException.getStatusCode());
        assertNotEquals("Job with id " + 12 + " not found. :)",  jobNotFoundException.getMessage());

        Mockito.verify(jobsRepository, Mockito.times(1))
                .getJobsById(jobs.getId());

    }

    @Test
    public void getAllJobsTest(){
        List<Jobs> jobsListWithKeyword = new ArrayList<>();
        jobsListWithKeyword.add(jobs);
        Page<Jobs> pageKeyword = new PageImpl<>(jobsListWithKeyword);

        List<Jobs> jobsListWithoutKeyWord = new ArrayList<>();
        jobsListWithoutKeyWord.add(jobs);
        Page<Jobs> allPage = new PageImpl<>(jobsListWithoutKeyWord);

        List<Jobs> noJobs = new ArrayList<>();
        Page<Jobs> noJobsPage = new PageImpl<>(noJobs);

        int pageNo = 1;
        int pageSize = 10;
        String keyword = "java";
        String noJobKeyword = "No Job";

        List<JobsDto> jobsDtosExpected = jobsListWithKeyword.stream()
                .map(JobsDto::fromJobs)
                .toList();

        Mockito.when(jobsRepository.getJobsByKeyWord( keyword ,PageRequest.of(pageNo,pageSize)))
                .thenReturn(pageKeyword);
        Mockito.when(jobsRepository.getAllJobs(PageRequest.of(pageNo, pageSize)))
                .thenReturn(allPage);
        Mockito.when(jobsRepository.getJobsByKeyWord( noJobKeyword ,PageRequest.of(pageNo,pageSize)))
                .thenReturn(noJobsPage);

        JobsResponse response = jobsService.getAllJobs(keyword, pageNo, pageSize);
        JobsResponse responseAllJobs = jobsService.getAllJobs(" ", pageNo, pageSize);
        JobNotFoundException jobNotFoundException = assertThrows(JobNotFoundException.class,
                () -> jobsService.getAllJobs(noJobKeyword, pageNo, pageSize));

        assertEquals(jobsDtosExpected.size(), response.getResponseContents().size());
        assertTrue(response.isLast());

        assertEquals(jobsDtosExpected.size(), responseAllJobs.getResponseContents().size());
        assertTrue(responseAllJobs.isLast());

        assertEquals("No jobs found. :(",  jobNotFoundException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, jobNotFoundException.getStatusCode());
        assertNotEquals("No jobs found. :)",  jobNotFoundException.getMessage());

        Mockito.verify(jobsRepository, Mockito.times(1))
                        .getJobsByKeyWord(keyword, PageRequest.of(pageNo, pageSize));
        Mockito.verify(jobsRepository, Mockito.times(1))
                .getAllJobs(PageRequest.of(pageNo, pageSize));
    }
}