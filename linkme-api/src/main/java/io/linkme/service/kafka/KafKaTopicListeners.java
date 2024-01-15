package io.linkme.service.kafka;

import io.linkme.model.JobListingDTO;
import io.linkme.model.ProfileModel;
import io.linkme.service.elasticSearch.ElasticSearchSearcherImpl;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log
public class KafKaTopicListeners {

    private final ElasticSearchSearcherImpl elasticSearchSearcherImpl;

    public KafKaTopicListeners(ElasticSearchSearcherImpl elasticSearchSearcherImpl) {
        this.elasticSearchSearcherImpl = elasticSearchSearcherImpl;
    }

    private final Logger logger = LoggerFactory.getLogger(KafKaTopicListeners.class);

    /**
     * Event will be received once recruiter post a job
     * @param jobListingDTO
     */
    @KafkaListener(topics = "${topic.job.name}", groupId = "job-group")
    public void consumeJobCreation(JobListingDTO jobListingDTO) {

        logger.info(String.format("New job is received : " + jobListingDTO));
        elasticSearchSearcherImpl.upsertJobInElasticSearch(jobListingDTO.getJobId(), jobListingDTO);
    }

    /**
     * Event will be received once candidate creates or updates his profile
     * @param profileModel
     */
    @KafkaListener(topics = "${topic.candidate.name}", groupId = "candidate-group")
    public void consumeCandidateProfileCreation(ProfileModel profileModel) {

        logger.info(String.format("New candidate profile is received : " + profileModel));
        elasticSearchSearcherImpl.upsertCandidateInElasticSearch(profileModel.getUserId(), profileModel);
    }
}
