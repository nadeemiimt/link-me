package io.linkme.service.user;

import io.linkme.domain.Profile;
import io.linkme.domain.SalaryComparison;
import io.linkme.model.Constants.SalaryComparisonStatus;
import io.linkme.model.SalaryComparisonDTO;
import io.linkme.model.SalaryComparisonEvent;
import io.linkme.repos.ProfileRepository;
import io.linkme.repos.SalaryComparisonRepository;
import io.linkme.service.contracts.ElasticSearchSearcher;
import io.linkme.service.elasticSearch.ElasticSearchSearcherImpl;
import io.linkme.service.kafka.KafkaProducerService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
public class SalaryComparisonService {

    private final Logger LOGGER = LoggerFactory.getLogger(SalaryComparisonService.class);

    @Value("${topic.salary.comparison.name}")
    private String salaryComparisonTopic;
    private final SalaryComparisonRepository salaryComparisonRepository;
    private final ProfileRepository profileRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ElasticSearchSearcher elasticSearchSearcher;

    public SalaryComparisonService(final SalaryComparisonRepository salaryComparisonRepository,
                                   final KafkaProducerService kafkaProducerService,
                                   final ElasticSearchSearcher elasticSearchSearcher,
                                   final ProfileRepository profileRepository) {
        this.salaryComparisonRepository = salaryComparisonRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.elasticSearchSearcher = elasticSearchSearcher;
        this.profileRepository = profileRepository;
    }

    public SalaryComparisonDTO findSalaryComparisonByProfileId(Integer profileId) {
        Optional<SalaryComparison> salaryComparisonOptional = getSalaryComparison(profileId);

        SalaryComparison tmpSalaryComparison = new SalaryComparison();
        if(salaryComparisonOptional.isPresent()) {
            SalaryComparison salaryComparison = salaryComparisonOptional.get();
            if(salaryComparison.getEmployeeMidPointSalaryForLocation() != null && salaryComparison.getEmployeeMidPointSalaryForLocation().floatValue() > 0 && salaryComparison.getStatus().equalsIgnoreCase(SalaryComparisonStatus.UPDATED.toString())) {
                Date currentDate = new Date();
                long duration  = currentDate.getTime() - salaryComparison.getUpdatedOn().getTime();

                long daysDiff = TimeUnit.MILLISECONDS.toDays(duration);

                if(daysDiff > 7) {
                    // send kafka message to update comparison data
                    updateStatusAndSendToKafka(profileId, tmpSalaryComparison, SalaryComparisonStatus.OUTDATED.name());
                } else {
                    tmpSalaryComparison = salaryComparison;
                }
            } else {
                // send kafka message to update comparison data
                updateStatusAndSendToKafka(profileId, tmpSalaryComparison, SalaryComparisonStatus.INPROGRESS.name());
            }
        } else {
            // send kafka message to update comparison data
            updateStatusAndSendToKafka(profileId, tmpSalaryComparison, SalaryComparisonStatus.INPROGRESS.name());
        }

        return mapToDTO(tmpSalaryComparison, new SalaryComparisonDTO());
    }

    public void upsert(Integer profileId) {
        try {
            Optional<SalaryComparison> salaryComparisonOptional = getSalaryComparison(profileId);

            if (salaryComparisonOptional.isPresent()) { // update
                SalaryComparison salaryComparison = salaryComparisonOptional.get();
                Profile profile = salaryComparison.getProfile();

                updateSalaryComparison(salaryComparison, profile);

                update(salaryComparison.getComparisonId(), salaryComparison);
            } else { // insert;
                Optional<Profile> profile = profileRepository.findByProfileIdAndActive(profileId, true);
                if(profile.isPresent()) {
                    SalaryComparison salaryComparison = new SalaryComparison();
                    salaryComparison.setStatus(SalaryComparisonStatus.UPDATED.name());
                    salaryComparison.setProfile(profile.get());
                    updateSalaryComparison(salaryComparison, profile.get());
                    create(salaryComparison);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error in updating profile. Details {}", ex);
        }
    }

    private void updateSalaryComparison(SalaryComparison salaryComparison, Profile profile) {
        if(profile != null && profile.getSalary() != null) {
            Double employeeMidPointSalaryForLocation = getAverageSalaryForLocationAndExperience(salaryComparison, profile);

            salaryComparison.setEmployeeMidPointSalaryForLocation(BigDecimal.valueOf(employeeMidPointSalaryForLocation));
        } else if (profile == null) {
            salaryComparison.setStatus(SalaryComparisonStatus.NOPROFILE.name());
        } else if (profile.getSalary() == null) {
            salaryComparison.setStatus(SalaryComparisonStatus.SALARYNOTENTERED.name());
        }
    }

    private Double getAverageSalaryForLocationAndExperience(SalaryComparison salaryComparison, Profile profile) {
        BigDecimal zero = new BigDecimal(0);
        BigDecimal from = salaryComparison.getProfile().getWorkExperience().compareTo(zero) > 0 ? salaryComparison.getProfile().getWorkExperience().subtract(BigDecimal.valueOf(2)) : zero;
        BigDecimal to = salaryComparison.getProfile().getWorkExperience().add(BigDecimal.valueOf(2));

        String field ="avg_salary";
        Double employeeMidPointSalaryForLocation = elasticSearchSearcher.compareCandidateSalary(buildSearchSource(profile.getLocation(), from, to, field), field);
        return employeeMidPointSalaryForLocation;
    }

    public Integer create(final SalaryComparison salaryComparison) {
        return salaryComparisonRepository.save(salaryComparison).getComparisonId();
    }

    public void update(final Integer comparisonId, final SalaryComparison salaryComparison) {
        salaryComparisonRepository.save(salaryComparison);
    }

    private static SearchSourceBuilder buildSearchSource(String location, BigDecimal fromExperience, BigDecimal toExperience, String field) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("location.keyword", location))
                .must(QueryBuilders.rangeQuery("experience").gte(fromExperience).lte(toExperience));

        sourceBuilder.query(query);

        AggregationBuilder aggregation = AggregationBuilders.avg(field).field("salary");
        sourceBuilder.aggregation(aggregation);

        return sourceBuilder;
    }

    private void updateStatusAndSendToKafka(Integer profileId, SalaryComparison tmpSalaryComparison, String status) {
        kafkaProducerService.send(salaryComparisonTopic, String.valueOf(profileId), new SalaryComparisonEvent(profileId));
        tmpSalaryComparison.setStatus(status);
    }

    private Optional<SalaryComparison> getSalaryComparison(Integer profileId) {
        Optional<SalaryComparison> salaryComparisonOptional = salaryComparisonRepository.findByProfile_ProfileId(profileId);
        return salaryComparisonOptional;
    }

    private SalaryComparisonDTO mapToDTO(final SalaryComparison salaryComparison,
            final SalaryComparisonDTO salaryComparisonDTO) {
        salaryComparisonDTO.setComparisonId(salaryComparison.getComparisonId());
        salaryComparisonDTO.setUpdatedOn(salaryComparison.getUpdatedOn());
        salaryComparisonDTO.setStatus(salaryComparison.getStatus());
        salaryComparisonDTO.setEmployeeMidPointSalaryForLocation(salaryComparison.getEmployeeMidPointSalaryForLocation());

        if(salaryComparison.getProfile() != null) {
            salaryComparisonDTO.setProfileId(salaryComparison.getProfile().getProfileId());
            salaryComparisonDTO.setSalaryAmount(salaryComparison.getProfile().getSalary());
            salaryComparisonDTO.setLocation(salaryComparison.getProfile().getLocation());
        }

        return salaryComparisonDTO;
    }

    private SalaryComparison mapToEntity(final SalaryComparisonDTO salaryComparisonDTO,
            final SalaryComparison salaryComparison) {
        salaryComparison.setComparisonId(salaryComparisonDTO.getComparisonId());
        salaryComparison.setStatus(salaryComparisonDTO.getStatus());
        salaryComparison.setEmployeeMidPointSalaryForLocation(salaryComparisonDTO.getEmployeeMidPointSalaryForLocation());
        return salaryComparison;
    }

}
