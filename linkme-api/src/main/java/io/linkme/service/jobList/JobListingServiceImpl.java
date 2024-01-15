package io.linkme.service.jobList;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.linkme.domain.*;
import io.linkme.model.Job;
import io.linkme.model.JobListingDTO;
import io.linkme.model.SkillDTO;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.JobSkillRepository;
import io.linkme.repos.UserJobApplicationRepository;
import io.linkme.repos.UserRepository;
import io.linkme.service.contracts.ElasticSearchSearcher;
import io.linkme.service.contracts.JobListingService;
import io.linkme.service.contracts.SkillService;
import io.linkme.service.kafka.KafkaProducerService;
import io.linkme.util.AuthHelper;
import io.linkme.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class JobListingServiceImpl implements JobListingService {

    private final Logger LOGGER = LoggerFactory.getLogger(JobListingServiceImpl.class);

    private final JobListingRepository jobListingRepository;
    private final ElasticSearchSearcher elasticSearchSearcher;

    private final KafkaProducerService kafkaProducerService;
    private final UserRepository userRepository;
    private final SkillService skillService;
    private final JobSkillRepository jobSkillRepository;
    private final UserJobApplicationRepository userJobApplicationRepository;

    @Value("${topic.job.name}")
    private String jobTopicName;

    ObjectMapper objectMapper = new ObjectMapper();

    public JobListingServiceImpl(final JobListingRepository jobListingRepository,
                                 final ElasticSearchSearcher elasticSearchSearcher,
                                 final KafkaProducerService kafkaProducerService,
                                 final JobSkillRepository jobSkillRepository,
                                 final SkillService skillService,
                                 final UserJobApplicationRepository userJobApplicationRepository,
                                 final UserRepository userRepository) {
        this.jobListingRepository = jobListingRepository;
        this.elasticSearchSearcher = elasticSearchSearcher;
        this.kafkaProducerService = kafkaProducerService;
        this.jobSkillRepository = jobSkillRepository;
        this.skillService = skillService;
        this.userJobApplicationRepository = userJobApplicationRepository;
        this.userRepository = userRepository;
    }

    /**
     * find all jobs using page number and size
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<JobListingDTO> findAll(int page, int size) {
        // Create a Pageable object with the requested page and size
        Pageable pageable = PageRequest.of(page, size, Sort.by("jobId"));

        // Fetch the paginated JobListings
        Page<JobListing> jobListings = jobListingRepository.findAll(pageable);

        User user = userRepository.findByEmail(AuthHelper.getUserName());
        // Fetch JobSkills and UserJobApplications for each JobListing
        List<JobListingDTO> jobListingDTOs = getJobListingDTOS(jobListings.getContent(), user);

        // Create a new Page with the updated list and the same Pageable information
        return new PageImpl<>(jobListingDTOs, pageable, jobListings.getTotalElements());
    }

    /**
     * search job using filter
     * @param jobListingDTO
     * @return
     * @throws IOException
     */
    @Override
    public List<JobListingDTO> findAllByKeywords(Job jobListingDTO) throws IOException {
        // Build the Elasticsearch query JSON dynamically
        String query = buildElasticsearchQuery(jobListingDTO.getSkills().toArray(new String[0]), jobListingDTO.getJobRole(), jobListingDTO.getJobType(), jobListingDTO.getLocation(), jobListingDTO.getExperience());
        List<Map<String, Object>> jobs = elasticSearchSearcher.searchJobs(query);
        List<Job> jobList = jobs.stream().map(x -> objectMapper.convertValue(x, Job.class)).toList();
        final List<JobListing> jobListings = jobListingRepository.findByJobIds(jobList.stream().map(x-> x.getJobId()).distinct().toList());
        User user = userRepository.findByEmail(AuthHelper.getUserName());


        // Fetch JobSkills for each JobListing
        return getJobListingDTOS(jobListings, user);
    }

    /**
     * get job by job id
     * @param jobId
     * @return
     */
    @Override
    public JobListingDTO get(final Integer jobId) {
        Optional<JobListing> jobListing =  jobListingRepository.findById(jobId);

        // Fetch JobSkills for each JobListing
        if (jobListing.isPresent()) {
            Set<JobSkill> jobSkills = jobSkillRepository.findByJob_JobId(jobListing.get().getJobId());
            jobListing.get().setSkills(jobSkills);
        }
        return jobListing.map(jl -> mapToDTO(jl, new JobListingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    /**
     * create job for recruiter
     * @param jobListingDTO
     * @return
     */
    @Override
    public Integer create(final JobListingDTO jobListingDTO) {
        final JobListing jobListing = new JobListing();

        mapToEntity(jobListingDTO, jobListing);

        Integer jobId = jobListingRepository.save(jobListing).getJobId();
        jobListingDTO.setJobId(jobId);

        saveSkills(jobListingDTO, jobListing);

        sendToKafka(jobListingDTO, jobId);

        return jobId;
    }

    /**
     * update job
     * @param jobId
     * @param jobListingDTO
     */
    @Override
    public void update(final Integer jobId, final JobListingDTO jobListingDTO) {
        final JobListing jobListing = jobListingRepository.findById(jobId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(jobListingDTO, jobListing);

        jobListingRepository.save(jobListing);
        jobListingDTO.setJobId(jobId);

        saveSkills(jobListingDTO, jobListing);

        sendToKafka(jobListingDTO, jobId);
    }

    /**
     * delete job
     * @param jobId
     */
    @Override
    public void delete(final Integer jobId) {
        jobListingRepository.deleteById(jobId);
    }

    private List<JobListingDTO> getJobListingDTOS(List<JobListing> jobListings, User user) {
        List<JobListingDTO> jobListingDTOs = jobListings.stream()
                .map(jobListing -> {
                    Set<JobSkill> jobSkills = jobSkillRepository.findByJob_JobId(jobListing.getJobId());
                    jobListing.setSkills(jobSkills);

                    JobListing jl = new JobListing();
                    jl.setJobId(jobListing.getJobId());
                    Optional<UserJobApplication> jobApplication = userJobApplicationRepository.findByUserAndJob(user, jl);

                    if (jobApplication.isPresent()) {
                        Set<UserJobApplication> userJobApplicationSet = new HashSet<>();
                        userJobApplicationSet.add(jobApplication.get());
                        jobListing.setJobUserJobApplications(userJobApplicationSet);
                    } else {
                        jobListing.setJobUserJobApplications(new HashSet<>());
                    }

                    return mapToDTO(jobListing, new JobListingDTO());
                })
                .collect(Collectors.toList());
        return jobListingDTOs;
    }

    private void saveSkills(JobListingDTO jobListingDTO, JobListing jobListing) {
        if(!CollectionUtils.isEmpty(jobListingDTO.getSkills())) {

            // Save Skills and JobSkills
            Set<JobSkill> jobSkills = new HashSet<>();

            List<String> skills = new ArrayList<>();

            jobListingDTO.getSkills().forEach(skillName -> {
                skills.add(skillName);
            });

            skillService.insertSkillsIfNotExist(skills, true);

            List<SkillDTO> skillSets = skillService.findAllSkillsBySkillNames(skills);

            Set<Skill> skillDataList = new HashSet<>();

            skillSets.forEach(skillSet -> {
                Skill skill = new Skill();
                skill.setSkillName(skillSet.getSkillName());
                skill.setSkillId(skillSet.getSkillId());
                skill.setActive(skillSet.getActive());

                skillDataList.add(skill);
            });

            saveJobSkill(jobListing, skillDataList);

            jobListing.setSkills(jobSkills);
        }
    }

    private void saveJobSkill(JobListing jobListing, Set<Skill> skills) {
        for (Skill skill : skills) {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setSkill(skill);
            jobSkill.setJob(jobListing);
            jobSkill.setActive(true); // Set default value
            jobSkillRepository.save(jobSkill);
        }
    }

    private void sendToKafka(JobListingDTO jobListingDTO, Integer jobId) {
        try {
            LOGGER.info("sending message to kafka");
            kafkaProducerService.send(jobTopicName, String.valueOf(jobId), jobListingDTO);
        } catch (Exception ex) {
            LOGGER.error("Send to kafka error. Details {}", ex);
        }
    }

    private JobListingDTO mapToDTO(final JobListing jobListing, final JobListingDTO jobListingDTO) {
        jobListingDTO.setJobId(jobListing.getJobId());
        jobListingDTO.setTitle(jobListing.getTitle());
        jobListingDTO.setCompany(jobListing.getCompany());
        jobListingDTO.setDescription(jobListing.getDescription());
        jobListingDTO.setLocation(jobListing.getLocation());
        jobListingDTO.setJobType(jobListing.getJobType());
        jobListingDTO.setJobRole(jobListing.getJobRole());
        jobListingDTO.setExperience(jobListing.getExperience());
        jobListingDTO.setSalary(jobListing.getSalary());
        jobListingDTO.setRequirements(jobListing.getRequirements());
        jobListingDTO.setOtherJobDetails(jobListing.getOtherJobDetails());
        jobListingDTO.setPostedOn(jobListing.getPostedOn());
        jobListingDTO.setJobLink(jobListing.getJobLink());
        jobListingDTO.setPostedBy(jobListing.getPostedBy());
        jobListingDTO.setApplicationStatus(setApplicationStatus(jobListing));

        jobListingDTO.setSkills(getSkills(jobListing));

        return jobListingDTO;
    }

    private static String setApplicationStatus(JobListing jobListing) {
        return jobListing.getJobUserJobApplications().isEmpty() ? "" : jobListing.getJobUserJobApplications().stream().findFirst().orElse(new UserJobApplication()).getStatus();
    }

    private static List<String> getSkills(JobListing jobListing) {
        List<String> result = null;

        if(!CollectionUtils.isEmpty(jobListing.getSkills())) {
            result = jobListing.getSkills().stream().map(x -> x.getSkill()).map(x -> x.getSkillName()).collect(Collectors.toList());
        }

        return result;
    }

    private JobListing mapToEntity(final JobListingDTO jobListingDTO, final JobListing jobListing) {
        jobListing.setTitle(jobListingDTO.getTitle());
        jobListing.setCompany(jobListingDTO.getCompany());
        jobListing.setDescription(jobListingDTO.getDescription());
        jobListing.setLocation(jobListingDTO.getLocation());
        jobListing.setExperience(jobListingDTO.getExperience());
        jobListing.setJobRole(jobListingDTO.getJobRole());
        jobListing.setJobType(jobListingDTO.getJobType());
        jobListing.setSalary(jobListingDTO.getSalary());
        jobListing.setRequirements(jobListingDTO.getRequirements());
        jobListing.setOtherJobDetails(jobListingDTO.getOtherJobDetails());
        jobListing.setJobLink(jobListingDTO.getJobLink());
        return jobListing;
    }

    // Method to build the Elasticsearch query JSON
    private static String buildElasticsearchQuery(String[] skills, String jobRole, String jobType, String location, BigDecimal experience) {
        // List to hold query parts
        List<String> queryParts = new ArrayList<>();

        // Add skills filter if not null and not empty
        if (skills != null && skills.length > 0) {
            StringBuilder skillsFilter = new StringBuilder();
            skillsFilter.append("        {\n");
            skillsFilter.append("          \"terms\": {\n");
            skillsFilter.append("            \"skills.keyword\": [\n");
            for (int i = 0; i < skills.length; i++) {
                skillsFilter.append("\"").append(skills[i]).append("\"");
                if (i < skills.length - 1) {
                    skillsFilter.append(", ");
                }
            }
            skillsFilter.append("]\n");
            skillsFilter.append("          }\n");
            skillsFilter.append("        }");
            queryParts.add(skillsFilter.toString());
        }

        // Add jobRole filter if not null
        if (!ObjectUtils.isEmpty(jobRole)) {
            queryParts.add("        {\n" +
                    "          \"match_phrase\": {\n" +
                    "            \"jobRole\": \"" + jobRole + "\"\n" +
                    "          }\n" +
                    "        }");
        }

        // Add jobType filter if not null
        if (!ObjectUtils.isEmpty(jobType)) {
            queryParts.add("        {\n" +
                    "          \"match_phrase\": {\n" +
                    "            \"jobType\": \"" + jobType + "\"\n" +
                    "          }\n" +
                    "        }");
        }

        // Add location filter if not null
        if (!ObjectUtils.isEmpty(location)) {
            queryParts.add("        {\n" +
                    "          \"match_phrase\": {\n" +
                    "            \"location\": \"" + location + "\"\n" +
                    "          }\n" +
                    "        }");
        }

        // Add experience range filter if not null
        if (!ObjectUtils.isEmpty(experience)) {
            queryParts.add("        {\n" +
                    "          \"range\": {\n" +
                    "            \"experience\": {\n" +
                    "              \"gte\": " + experience + "\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }");
        }

        // Constructing the final query
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("{\n");
      //  queryBuilder.append("  \"query\": {\n");
        queryBuilder.append("    \"bool\": {\n");
        queryBuilder.append("      \"must\": [\n");

        // Append all query parts
        for (int i = 0; i < queryParts.size(); i++) {
            queryBuilder.append(queryParts.get(i));
            if (i < queryParts.size() - 1) {
                queryBuilder.append(",\n");
            }
        }

        queryBuilder.append("\n");
        queryBuilder.append("      ]\n");
        queryBuilder.append("    }\n");
    //    queryBuilder.append("  }\n");
        queryBuilder.append("}");

        return queryBuilder.toString();
    }

}
