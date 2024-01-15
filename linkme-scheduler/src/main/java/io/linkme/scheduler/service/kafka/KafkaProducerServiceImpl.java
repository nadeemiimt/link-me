package io.linkme.scheduler.service.kafka;

import io.linkme.scheduler.service.contracts.KafkaProducerService;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * send kafka message
     * @param topicName
     * @param key
     * @param value
     */
    @Override
    public void send(String topicName, String key, Object value) {

        var future = kafkaTemplate.send(topicName, key, value);

        future.whenComplete((sendResult, exception) -> {
            if (exception != null) {
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            LOGGER.info("New Job listing send to Kafka topic : "+ value);
        });
    }
}
