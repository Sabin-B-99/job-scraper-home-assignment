package com.homeassignment.jobscraper.repositories;

import com.homeassignment.jobscraper.entities.Jobs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobsRepository extends PagingAndSortingRepository<Jobs, Integer>, CrudRepository<Jobs, Integer> {

    @Query(value = "SELECT * FROM jobs ", countQuery = "SELECT COUNT(*) FROM jobs", nativeQuery = true)
    List<Jobs> getAllJobs(Pageable pageable);

    Optional<Jobs> getJobsById(@Param("id") int id);

}
