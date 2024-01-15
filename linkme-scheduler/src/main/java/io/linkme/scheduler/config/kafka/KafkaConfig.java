package io.linkme.scheduler.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${topic.scheduler.name}")
    private String schedulerTopicName;
    @Value("${topic.scheduler.userJob.name}")
    private String schedulerUserJobTopicName;
    @Value("${topic.scheduler.notification.name}")
    private String schedulerNotificationTopicName;

    @Bean
    public NewTopic schedulerTopicTask() {
        return TopicBuilder.name(schedulerTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userJobMappingTopicTask() {
        return TopicBuilder.name(schedulerUserJobTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationTopicTask() {
        return TopicBuilder.name(schedulerNotificationTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
