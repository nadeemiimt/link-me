package io.linkme.scheduler.service.elasticSearch;

import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticSearchClient implements AutoCloseable {

    @Value("${es.host}")
    private String host;

    @Value("${es.port}")
    private Integer port;

    @Value("${es.scheme}")
    private String scheme;

    private RestHighLevelClient client;

    @PostConstruct
    public void init() {
        try {
            RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, scheme));
            client = new RestHighLevelClient(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean indexExists(String indexName) throws IOException {
        return client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
    }

    public RestHighLevelClient getClient() {
        return client;
    }

    public void close() throws Exception {
        client.close();
    }
}
