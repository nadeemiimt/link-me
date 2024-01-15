package io.linkme.scheduler.service.kafka;

import io.linkme.scheduler.model.NotificationData;
import io.linkme.scheduler.model.ProfileInfo;
import io.linkme.scheduler.model.SchedulerEvent;
import io.linkme.scheduler.service.NotificationServiceImpl;
import io.linkme.scheduler.service.elasticSearch.ElasticSearchSearcherImpl;
import io.linkme.scheduler.service.recommendation.UserRecommendationServiceImpl;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log
public class KafKaTopicListeners {

    private final ElasticSearchSearcherImpl elasticSearchSearcherImpl;

    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;
    private final NotificationServiceImpl notificationService;
    private final UserRecommendationServiceImpl userRecommendationService;

    public KafKaTopicListeners(ElasticSearchSearcherImpl elasticSearchSearcherImpl, KafkaProducerServiceImpl kafkaProducerServiceImpl, NotificationServiceImpl notificationService, UserRecommendationServiceImpl userRecommendationService) {
        this.elasticSearchSearcherImpl = elasticSearchSearcherImpl;
        this.kafkaProducerServiceImpl = kafkaProducerServiceImpl;
        this.notificationService = notificationService;
        this.userRecommendationService = userRecommendationService;
    }

    private final Logger logger = LoggerFactory.getLogger(KafKaTopicListeners.class);

    @KafkaListener(topics = "${topic.scheduler.name}", groupId = "scheduler-group")
    public void consumeSchedulerEventCreation(SchedulerEvent schedulerEvent) {

        logger.info(String.format("New scheduler event is received : " + schedulerEvent));
        userRecommendationService.findEachActiveUserAndTheirRecommendedJobs();
    }

    @KafkaListener(topics = "${topic.scheduler.userJob.name}", groupId = "userJob-group")
    public void consumeCandidateProfileRecommendationCreation(ProfileInfo profileInfo) throws IOException {

        logger.info(String.format("Candidate profile is received for recommendation: " + profileInfo));

        userRecommendationService.findMatchJobForUserProfile(profileInfo);
    }

    @KafkaListener(topics = "${topic.scheduler.notification.name}", groupId = "notification-group")
    public void consumeNotification(NotificationData notificationData) throws IOException {

        logger.info(String.format("Recommendation notification is received: " + notificationData));

        notificationService.sendRecommendationNotification(notificationData);
    }
}
