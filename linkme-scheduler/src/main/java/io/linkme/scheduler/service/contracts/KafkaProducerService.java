package io.linkme.scheduler.service.contracts;

public interface KafkaProducerService {
    void send(String topicName, String key, Object value);
}
