package io.linkme.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.linkme.scheduler.model.NotificationData;
import io.linkme.scheduler.service.contracts.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    ObjectMapper objectMapper = new ObjectMapper();
    @Value("${courier.api.url}")
    private String courierApiUrl;

    @Value("${courier.api.token}")
    private String courierApiToken;

    /**
     * send email notification
     * @param notificationData
     * @throws JsonProcessingException
     */
    @Override
    public void sendRecommendationNotification(NotificationData notificationData) throws JsonProcessingException {
        String details = objectMapper.writeValueAsString(notificationData.getJobListingNotificationDataList());
        String jobDetailsString = "{ " +
                "\"jobDetails\": " +
                "\"" + details + "\"" +
                "}" +
                "}";

        try {
            sendMessage(notificationData.getEmail(), notificationData.getUserFullName() + " Job Recommendation for Today! ", "Below are the job details in json format: {{jobDetails}}", jobDetailsString);
        } catch (Exception ex) {
            LOGGER.error("Error in sending email. Details {}", ex);
        }
    }


    private void sendMessage(String email, String title, String body, String data) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + courierApiToken);

        String jsonBody = "{ " + // can be refined for template
                "\"message\": {" +
                "\"to\": {\"email\":\"" + email + "\"}," +
                "\"content\": {" +
                "\"title\": \"" + title + "\"," +
                "\"body\": \"" + body + "\"" +
                "}," +
                "\"data\": " + data +
                "}" +
                "}";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(courierApiUrl, jsonBody, String.class);
        LOGGER.info("Response: " + responseEntity.getBody());
    }
}
