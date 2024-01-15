package io.linkme.service.contracts;

import io.linkme.model.Job;
import io.linkme.model.JobListingDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface JobListingService {
    Page<JobListingDTO> findAll(int page, int size);

    List<JobListingDTO> findAllByKeywords(Job jobListingDTO) throws IOException;

    JobListingDTO get(Integer jobId);

    Integer create(JobListingDTO jobListingDTO);

    void update(Integer jobId, JobListingDTO jobListingDTO);

    void delete(Integer jobId);
}
