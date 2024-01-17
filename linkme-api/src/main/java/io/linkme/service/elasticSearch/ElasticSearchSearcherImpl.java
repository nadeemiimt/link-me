package io.linkme.service.elasticSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.linkme.domain.JobListing;
import io.linkme.model.JobListingDTO;
import io.linkme.model.ProfileModel;
import io.linkme.service.contracts.ElasticSearchSearcher;
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
import org.elasticsearch.search.aggregations.metrics.Avg;
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

    /**
     * search jobs
     * @param query
     * @return
     * @throws IOException
     */
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

    public Double compareCandidateSalary(SearchSourceBuilder searchSourceBuilder, String responseField) {
        Double response = null;
        try {
            SearchRequest searchRequest = new SearchRequest(CANDIDATE_INDEX_NAME);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = esClient.getClient().search(searchRequest, RequestOptions.DEFAULT);

            // Accessing the aggregation result
            Avg avgAggregation = searchResponse.getAggregations().get(responseField);

            // Checking if the aggregation result is not null
            if (avgAggregation != null) {
                // Accessing the average salary value
                response = avgAggregation.getValue();
            } else {
                LOGGER.info("Response value is {}", response);
            }

            // Process the search response as needed
        } catch (IOException e) {
            LOGGER.error("Error in es salary comparison query", e);
        }

        return response;
    }

    /**
     * update or insert jobs
     * @param job
     * @param documentKey
     * @param indexName
     * @return
     * @throws IOException
     */
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

    /**
     * upsertJobInElasticSearch
     * @param jobId
     * @param jobListingDTO
     * @return
     */
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

    /**
     * upsertJobInElasticSearch
     * @param jobId
     * @param jobListing
     * @param skills
     * @return
     */
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

    /**
     * upsertCandidateInElasticSearch
     * @param userId
     * @param userDTO
     * @return
     */
    @Override
    public Boolean upsertCandidateInElasticSearch(Integer userId, ProfileModel userDTO) {
        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);
        map.put("skills", userDTO.getSkills().stream().map(skill-> combineWordsWithHyphen(skill)).collect(Collectors.toList()));
        map.put("location", combineWordsWithHyphen(userDTO.getLocation()));
        map.put("experience", userDTO.getWorkExperience());
        map.put("salary", userDTO.getSalary());
        map.put("currencyCode", userDTO.getCurrencyCode());

        return upsertRecords(map, "userId", CANDIDATE_INDEX_NAME);
    }

    /**
     * upsertRecords
     * @param map
     * @param key
     * @param indexName
     * @return
     */
    private Boolean upsertRecords(Map<String, Object> map, String key, String indexName) {
        try {
            LOGGER.info("seeding new job type to elastic search");
            return upsertRecord(map, key, indexName);
        } catch (IOException e) {
            LOGGER.info("seeding new job type to elastic search error. Details {}", e);
            throw new RuntimeException(e);
        }
    }

    public static String combineWordsWithHyphen(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");

        // Combine words with hyphens
        String result = String.join("-", words);

        return result;
    }

}
