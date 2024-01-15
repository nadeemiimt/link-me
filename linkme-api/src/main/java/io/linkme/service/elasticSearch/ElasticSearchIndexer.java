package io.linkme.service.elasticSearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchIndexer {

    @Value("${es.job.index}")
    private String JOB_INDEX_NAME;
    @Value("${es.candidate.index}")
    private String CANDIDATE_INDEX_NAME;

    @Value("${es.seed.from.file}")
    private Boolean seedFromFile;
    private ElasticSearchClient esClient;
    private ElasticSearchSearcherImpl elasticSearchSearcherImpl;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ElasticSearchIndexer(ElasticSearchClient esClient, ElasticSearchSearcherImpl elasticSearchSearcherImpl) {
        this.esClient = esClient;
        this.elasticSearchSearcherImpl = elasticSearchSearcherImpl;
    }

    public void createIndexIfNotExist(String indexName) throws IOException {
        if (!esClient.indexExists(indexName)) {
            System.out.println("Index does not exist. Creating it.");
            esClient.getClient().indices().create(new CreateIndexRequest(indexName), RequestOptions.DEFAULT);
        } else {
            System.out.println("Index already exists.");
        }
    }

    @Bean
    public void indexJobs() throws IOException {
        String indexName = JOB_INDEX_NAME;
        String fileName = "jobs.json";
        String documentKey = "jobId";
        upsertRecords(indexName, fileName, documentKey);
    }


    @Bean
    public void indexCandidates() throws IOException {
        String indexName = CANDIDATE_INDEX_NAME;
        String fileName = "candidates.json";
        String documentKey = "userId";
        upsertRecords(indexName, fileName, documentKey);
    }

    private void upsertRecords(String indexName, String fileName, String documentKey) throws IOException {
        if (!esClient.indexExists(indexName)) {
            createIndexIfNotExist(indexName);

            if (seedFromFile) {
                // Reading the JSON from the classpath
                InputStream is = ElasticSearchIndexer.class.getClassLoader().getResourceAsStream(fileName);
                List<Map<String, Object>> values = objectMapper.readValue(is, new TypeReference<>() {
                });

                for (Map<String, Object> value : values) {
                    elasticSearchSearcherImpl.upsertRecord(value, documentKey, indexName);
                }
            }
        }
    }
}
