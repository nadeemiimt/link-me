package io.linkme.service.fileStorage;

import io.linkme.service.contracts.FileIoService;
import io.linkme.service.kafka.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class FileIoServiceImpl implements FileIoService {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${temp.file.io.key}")
    private String bearerToken;

    @Value("${file.io.url}")
    private String apiUrl;

    /**
     * Upload to file io server
     * @param file
     * @return
     */
    @Override
    public String uploadFile(File file) {
        String fileIoLink = null;
        try {
            // Create headers with authorization and content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(bearerToken);

            // Create a MultiValueMap representing the form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));
            body.add("expires", getTimestampNHoursLater(23));
            body.add("maxDownloads", "1");
            body.add("autoDelete", "true");

            // Create the request entity with headers and body
            RequestEntity<MultiValueMap<String, Object>> requestEntity =
                    new RequestEntity<>(body, headers, HttpMethod.POST, new URI(apiUrl));

            // Create a RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            // Perform the HTTP request
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            // Extract and print the response body (File.io URL)
            String responseBody = Objects.requireNonNull(responseEntity.getBody());
            System.out.println("File.io Response: " + responseBody);

            // Extract link from JSON response
            fileIoLink = responseBody.split("\"link\":")[1].split(",")[0].replace("\"", "");

        } catch (Exception e) {

            LOGGER.error("File upload error. Details {}", e);
        }

        return fileIoLink;
    }

    /**
     * download file from file io server
     * @param fileKey
     * @return
     */
    @Override
    public byte[] downloadFile(String fileKey) {
        String url = apiUrl + fileKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                byte[].class
        );

        return responseEntity.getBody();
    }

    public static String getTimestampNHoursLater(int n) {
        // Get the current time
        ZonedDateTime currentTime = ZonedDateTime.now();

        // Add 23 hours to the current time
        ZonedDateTime futureTime = currentTime.plusHours(n);

        // Format the future time as a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedTime = futureTime.format(formatter);

        return formattedTime;
    }
}
