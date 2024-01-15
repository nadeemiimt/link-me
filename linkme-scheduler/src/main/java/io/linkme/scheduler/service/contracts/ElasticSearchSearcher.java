package io.linkme.scheduler.service.contracts;

import io.linkme.scheduler.domain.JobListing;
import io.linkme.scheduler.model.JobListingDTO;
import io.linkme.scheduler.model.ProfileModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ElasticSearchSearcher {
    List<Map<String, Object>> searchJobs(String query) throws IOException;

    Boolean upsertRecord(Map<String, Object> job, String documentKey, String indexName) throws IOException;

    Boolean upsertJobInElasticSearch(Integer jobId, JobListingDTO jobListingDTO);

    Boolean upsertJobInElasticSearch(Integer jobId, JobListing jobListing, List<String> skills);

    Boolean upsertCandidateInElasticSearch(Integer userId, ProfileModel userDTO);
}
