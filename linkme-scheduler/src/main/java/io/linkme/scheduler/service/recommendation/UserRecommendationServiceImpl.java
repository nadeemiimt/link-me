package io.linkme.scheduler.service.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.linkme.scheduler.domain.JobListing;
import io.linkme.scheduler.model.Job;
import io.linkme.scheduler.model.JobListingNotificationData;
import io.linkme.scheduler.model.NotificationData;
import io.linkme.scheduler.model.ProfileInfo;
import io.linkme.scheduler.repos.JobListingRepository;
import io.linkme.scheduler.repos.ProfileRepository;
import io.linkme.scheduler.service.contracts.UserRecommendationService;
import io.linkme.scheduler.service.elasticSearch.ElasticSearchSearcherImpl;
import io.linkme.scheduler.service.kafka.KafkaProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserRecommendationServiceImpl implements UserRecommendationService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserRecommendationServiceImpl.class);

    @Value("${topic.scheduler.userJob.name}")
    private String schedulerUserDetailsTopic;

    private final JobListingRepository jobListingRepository;
    private final ElasticSearchSearcherImpl elasticSearchSearcherImpl;

    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;
    private final ProfileRepository profileRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public UserRecommendationServiceImpl(JobListingRepository jobListingRepository, ElasticSearchSearcherImpl elasticSearchSearcherImpl, KafkaProducerServiceImpl kafkaProducerServiceImpl, ProfileRepository profileRepository) {
        this.jobListingRepository = jobListingRepository;
        this.elasticSearchSearcherImpl = elasticSearchSearcherImpl;
        this.kafkaProducerServiceImpl = kafkaProducerServiceImpl;
        this.profileRepository = profileRepository;
    }

    /**
     * find each user who have active profile and send them to kafka for further processing
     */
    @Override
    public void findEachActiveUserAndTheirRecommendedJobs() {
        List<ProfileInfo> profileInfoList = profileRepository.findUserEmailNameAndSkillsWithNonNullProfile();

        if(profileInfoList != null && profileInfoList.size() > 0) {
            profileInfoList.forEach(profileInfo -> {
                kafkaProducerServiceImpl.send(schedulerUserDetailsTopic, profileInfo.getUserEmail(), profileInfo);
            });
        }
    }

    /**
     * find matching job for each matching jobs
     * @param profileInfo
     * @throws IOException
     */
    @Override
    public void findMatchJobForUserProfile(ProfileInfo profileInfo) throws IOException {
        List<String> skills = profileInfo.getUserSkills();
        String queryString = buildQueryString(skills);

        List<Map<String, Object>> jobs = elasticSearchSearcherImpl.searchJobs(queryString);
        List<Job> jobList = jobs.stream().map(x -> objectMapper.convertValue(x, Job.class)).toList();
        final List<JobListing> jobListings = jobListingRepository.findByJobIds(jobList.stream().map(x-> x.getJobId()).distinct().toList());

        if(jobListings != null && jobListings.size() > 0) {
            NotificationData notificationData = new NotificationData();
            List<JobListingNotificationData> jobListingNotificationDataList =  jobListings.stream().map(jobListing -> new JobListingNotificationData(jobListing.getJobId(), jobListing.getTitle(), jobListing.getJobLink())).collect(Collectors.toList());
            notificationData.setJobListingNotificationDataList(jobListingNotificationDataList);
            notificationData.setUserFullName(profileInfo.getFirstName() + " " + profileInfo.getLastName());
            notificationData.setEmail(profileInfo.getUserEmail());

        } else {
            LOGGER.info("No recommendation found for user {}", profileInfo);
        }
    }

    private static String buildQueryString(List<String> skills) {
        return "{ \"bool\": { \"must\": [ { \"terms\": { \"skills\": " + buildSkillsArray(skills) + " } } ] } }";
    }

    private static String buildSkillsArray(List<String> skills) {
        StringBuilder array = new StringBuilder("[");
        for (int i = 0; i < skills.size(); i++) {
            array.append("\"").append(skills.get(i)).append("\"");
            if (i < skills.size() - 1) {
                array.append(",");
            }
        }
        array.append("]");
        return array.toString();
    }

}
