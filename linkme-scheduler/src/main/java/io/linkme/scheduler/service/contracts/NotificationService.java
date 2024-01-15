package io.linkme.scheduler.service.contracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.linkme.scheduler.model.NotificationData;

public interface NotificationService {
    void sendRecommendationNotification(NotificationData notificationData) throws JsonProcessingException;
}
