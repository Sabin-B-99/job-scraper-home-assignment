package com.homeassignment.jobscraper.repositories;

import com.homeassignment.jobscraper.entities.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobsRepository extends PagingAndSortingRepository<Jobs, Integer>, CrudRepository<Jobs, Integer> {

    @Query(value = "SELECT * FROM jobs ", countQuery = "SELECT COUNT(*) FROM jobs", nativeQuery = true)
    Page<Jobs> getAllJobs(Pageable pageable);

    @Query(value = "SELECT * FROM jobs WHERE jobs.id = :id", nativeQuery = true)
    Optional<Jobs> getJobsById(@Param("id") int id);

    @Query(value = "SELECT * FROM jobs WHERE jobs.job_title ILIKE CONCAT('%',:keyword,'%') OR jobs.job_description ILIKE CONCAT('%',:keyword,'%')",
        countQuery = "SELECT COUNT(*) FROM jobs WHERE jobs.job_title ILIKE CONCAT('%',:keyword,'%') OR jobs.job_description ILIKE CONCAT('%',:keyword,'%')",
        nativeQuery = true)
    Page<Jobs> getJobsByKeyWord(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM jobs WHERE jobs.duplicate_check_hash = :hashVal", nativeQuery = true)
    Jobs getDuplicateCheckHashById(@Param("hashVal") String hashVal);
}
