package io.linkme.scheduler.service.recommendation;

import io.linkme.scheduler.model.SchedulerEvent;
import io.linkme.scheduler.service.kafka.KafkaProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class RecommendationEngineImpl {
    private final Logger LOGGER = LoggerFactory.getLogger(RecommendationEngineImpl.class);

    @Value("${topic.scheduler.name}")
    private String schedulerTopicName;

    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;

    public RecommendationEngineImpl(KafkaProducerServiceImpl kafkaProducerServiceImpl) {
        this.kafkaProducerServiceImpl = kafkaProducerServiceImpl;
    }

    /**
     * scheduler service starting point for processing
     * @throws InterruptedException
     */

    @Scheduled(cron = "${interval-in-cron}")
    public void initiateRecommendationProcess() throws InterruptedException {
        Long epochTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        LOGGER.info("recommendation started at " + epochTime);

        kafkaProducerServiceImpl.send(schedulerTopicName, String.valueOf(epochTime), new SchedulerEvent(epochTime));
    }
}
