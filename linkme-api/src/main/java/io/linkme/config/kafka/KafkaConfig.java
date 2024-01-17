package io.linkme.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${topic.job.name}")
    private String jobTopicName;

    @Value("${topic.candidate.name}")
    private String candidateTopicName;
    @Value("${topic.salary.comparison.name}")
    private String salaryComparisonTopicName;

    @Bean
    public NewTopic jobTopicTask() {
        return TopicBuilder.name(jobTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic candidateTopicTask() {
        return TopicBuilder.name(candidateTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic salaryComparisonTopicTask() {
        return TopicBuilder.name(salaryComparisonTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
