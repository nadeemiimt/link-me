package io.linkme.scheduler.service.elasticSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.linkme.scheduler.domain.JobListing;
import io.linkme.scheduler.model.JobListingDTO;
import io.linkme.scheduler.model.ProfileModel;
import io.linkme.scheduler.service.contracts.ElasticSearchSearcher;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ElasticSearchSearcherImpl implements ElasticSearchSearcher {

    private final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSearcherImpl.class);

    @Value("${es.job.index}")
    private String JOB_INDEX_NAME;

    @Value("${es.candidate.index}")
    private String CANDIDATE_INDEX_NAME;

    ElasticSearchClient esClient;

    ObjectMapper objectMapper = new ObjectMapper();

    public ElasticSearchSearcherImpl(ElasticSearchClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public List<Map<String, Object>> searchJobs(String query) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.wrapperQuery(query));

        SearchRequest searchRequest = new SearchRequest(JOB_INDEX_NAME);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = esClient.getClient().search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            results.add(hit.getSourceAsMap());
        }

        return results;
    }

    @Override
    public Boolean upsertRecord(Map<String, Object> job, String documentKey, String indexName) throws IOException {
        String id = String.valueOf(job.get(documentKey));

        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, id);

            String jsonString = objectMapper.writeValueAsString(job);
            updateRequest.doc(jsonString, XContentType.JSON);
            updateRequest.docAsUpsert(true);

            UpdateResponse updateResponse = esClient.getClient().update(
                    updateRequest, RequestOptions.DEFAULT);


            return updateResponse.getResult() == DocWriteResponse.Result.CREATED || updateResponse.getResult() == DocWriteResponse.Result.UPDATED;
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                return false;
            }
        }

        return false;
    }

    @Override
    public Boolean upsertJobInElasticSearch(Integer jobId, JobListingDTO jobListingDTO) {
        Map<String, Object> map = new HashMap<>();

        map.put("jobId", jobId);
        map.put("skills", jobListingDTO.getSkills().stream().map(skill-> combineWordsWithHyphen(skill)).collect(Collectors.toList()));
        map.put("jobRole", combineWordsWithHyphen(jobListingDTO.getJobRole()));
        map.put("jobType", combineWordsWithHyphen(jobListingDTO.getJobType()));
        map.put("location", combineWordsWithHyphen(jobListingDTO.getLocation()));
        map.put("experience", jobListingDTO.getExperience());

        return upsertRecords(map, "jobId", JOB_INDEX_NAME);
    }

    @Override
    public Boolean upsertJobInElasticSearch(Integer jobId, JobListing jobListing, List<String> skills) {
        Map<String, Object> map = new HashMap<>();

        map.put("jobId", jobId);
        map.put("skills", skills.stream().map(skill-> combineWordsWithHyphen(skill)).collect(Collectors.toList()));
        map.put("jobRole", combineWordsWithHyphen(jobListing.getJobRole()));
        map.put("jobType", combineWordsWithHyphen(jobListing.getJobType()));
        map.put("location", combineWordsWithHyphen(jobListing.getLocation()));
        map.put("experience", jobListing.getExperience());

        return upsertRecords(map, "jobId", JOB_INDEX_NAME);
    }

    @Override
    public Boolean upsertCandidateInElasticSearch(Integer userId, ProfileModel userDTO) {
        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);
        map.put("skills", userDTO.getSkills().stream().map(skill-> combineWordsWithHyphen(skill)).collect(Collectors.toList()));
        map.put("location", combineWordsWithHyphen(userDTO.getLocation()));
        map.put("experience", userDTO.getWorkExperience());

        return upsertRecords(map, "userId", CANDIDATE_INDEX_NAME);
    }

    private Boolean upsertRecords(Map<String, Object> map, String key, String indexName) {
        try {
            LOGGER.info("seeding new job type to elastic search");
            return upsertRecord(map, key, indexName);
        } catch (IOException e) {
            LOGGER.info("seeding new job type to elastic search error. Details {}", e);
            throw new RuntimeException(e);
        }
    }

    private static String combineWordsWithHyphen(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");

        // Combine words with hyphens
        String result = String.join("-", words);

        return result;
    }

}
