package io.linkme;

import io.linkme.seed.InitialSeed;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.JobSkillRepository;
import io.linkme.repos.SkillRepository;
import io.linkme.repos.UserRepository;
import io.linkme.service.common.SkillServiceImpl;
import io.linkme.service.elasticSearch.ElasticSearchSearcherImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class LinkmeApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkmeApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(LinkmeApplication.class, args);
    }

    @Bean
    ApplicationRunner init(UserRepository userRepository,
                           JobListingRepository jobListingRepository,
                           SkillServiceImpl skillServiceImpl,
                           SkillRepository skillRepository,
                           JobSkillRepository jobSkillRepository,
                           ElasticSearchSearcherImpl elasticSearchSearcherImpl) {
        return (ApplicationArguments args) ->  InitialSeed.dataSetup(userRepository, jobListingRepository, skillServiceImpl, skillRepository, jobSkillRepository, elasticSearchSearcherImpl);
    }
}
