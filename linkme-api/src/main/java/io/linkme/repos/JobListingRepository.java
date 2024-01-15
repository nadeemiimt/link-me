package io.linkme.repos;

import io.linkme.domain.JobListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface JobListingRepository extends JpaRepository<JobListing, Integer> {
    @Query( "select o from JobListing o where jobId in :ids order by jobId" )
    List<JobListing> findByJobIds(@Param("ids") List<Integer> jobIdList);
}
