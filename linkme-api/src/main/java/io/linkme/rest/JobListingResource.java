package io.linkme.rest;

import io.linkme.config.hazelcast.HazelCastConfig;
import io.linkme.model.Job;
import io.linkme.model.JobListingDTO;
import io.linkme.service.jobList.JobListingServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/jobListings", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobListingResource {

    private final JobListingServiceImpl jobListingServiceImpl;

    private final HazelCastConfig hazelCastConfig;

    public JobListingResource(final JobListingServiceImpl jobListingServiceImpl, HazelCastConfig hazelCastConfig) {
        this.jobListingServiceImpl = jobListingServiceImpl;
        this.hazelCastConfig = hazelCastConfig;
    }

    @GetMapping
    public ResponseEntity<Page<JobListingDTO>> getAllJobListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate and sanitize input parameters
        if (page < 0) {
            page = 0;
        }

        if (size < 1 || size > 100) { // Adjust the maximum size as needed
            size = 10;
        }

        // Fetch the paginated job listings
        Page<JobListingDTO> jobListingsPage = jobListingServiceImpl.findAll(page, size);

        // Return the paginated result in the ResponseEntity
        return ResponseEntity.ok(jobListingsPage);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobListingDTO> getJobListing(
            @PathVariable(name = "jobId") final Integer jobId) {
        JobListingDTO jobListingDTO = hazelCastConfig.get(jobId);

        if(jobListingDTO != null) {
            return ResponseEntity.ok(jobListingDTO);
        } else {
            return ResponseEntity.ok(hazelCastConfig.put(jobId, jobListingServiceImpl.get(jobId)));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<JobListingDTO>> getJobListingByKeywords(
            @RequestBody @Valid final Job jobListingDTO) throws IOException {
        return ResponseEntity.ok(jobListingServiceImpl.findAllByKeywords(jobListingDTO));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createJobListing(
            @RequestBody @Valid final JobListingDTO jobListingDTO) {
        final Integer createdJobId = jobListingServiceImpl.create(jobListingDTO);
        return new ResponseEntity<>(createdJobId, HttpStatus.CREATED);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<Integer> updateJobListing(
            @PathVariable(name = "jobId") final Integer jobId,
            @RequestBody @Valid final JobListingDTO jobListingDTO) {
        jobListingServiceImpl.update(jobId, jobListingDTO);
        return ResponseEntity.ok(jobId);
    }

    @DeleteMapping("/{jobId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteJobListing(
            @PathVariable(name = "jobId") final Integer jobId) {
        jobListingServiceImpl.delete(jobId);
        return ResponseEntity.noContent().build();
    }

}
