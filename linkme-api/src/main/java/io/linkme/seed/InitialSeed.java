package io.linkme.seed;

import io.linkme.domain.JobListing;
import io.linkme.domain.JobSkill;
import io.linkme.domain.Skill;
import io.linkme.domain.User;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.JobSkillRepository;
import io.linkme.repos.SkillRepository;
import io.linkme.repos.UserRepository;
import io.linkme.service.common.SkillServiceImpl;
import io.linkme.service.elasticSearch.ElasticSearchSearcherImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InitialSeed {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialSeed.class);

    public static void dataSetup(UserRepository userRepository,
                                 JobListingRepository jobListingRepository,
                                 SkillServiceImpl skillServiceImpl,
                                 SkillRepository skillRepository,
                                 JobSkillRepository jobSkillRepository,
                                 ElasticSearchSearcherImpl elasticSearchSearcherImpl) {
        //inserts
        skillSetup(skillServiceImpl);
        Integer recruiterUserId = userSetup(userRepository);
        jobSetup(jobListingRepository, skillRepository, jobSkillRepository, elasticSearchSearcherImpl, recruiterUserId);
    }

    private static void skillSetup(SkillServiceImpl skillServiceImpl) {
        List<String> skills = List.of("java",
                "c#",
                "Java Script",
                "json",
                "html",
                "html5",
                "css",
                "python",
                "golang",
                "team management");

        List<Integer> skillIds = skillServiceImpl.insertSkillsIfNotExist(skills, true);



        LOGGER.info("skills are saved with skill ids: {}", skillIds);
    }

    private static Integer userSetup(UserRepository repository) {
        User candidate = new User();
        candidate.setEmail("nadeemiimt@gmail.com");
        candidate.setPassword("test1");
        candidate.setName("Mohd Nadeem");
        candidate.setRecruiter(false);
        saveUserIfNotExists(repository, candidate);

        User recruiter = new User();
        recruiter.setEmail("companyhr@gmail.com");
        recruiter.setName("Company HR");
        recruiter.setPassword("test2");
        recruiter.setRecruiter(true);
        saveUserIfNotExists(repository, recruiter);

        User candidate1 = new User();
        candidate.setEmail("nadeemiimt1@gmail.com");
        candidate.setPassword("test1");
        candidate.setName("Test2");
        candidate.setRecruiter(false);
        saveUserIfNotExists(repository, candidate1);

        User candidate2 = new User();
        candidate.setEmail("nadeemiimt2@gmail.com");
        candidate.setPassword("test1");
        candidate.setName("Test3");
        candidate.setRecruiter(false);
        saveUserIfNotExists(repository, candidate2);


        return recruiter.getUserId();
    }

    private static void saveUserIfNotExists(UserRepository repository, User user) {
        ExampleMatcher NAME_MATCHER = ExampleMatcher.matching()
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.ignoreCase());
        Example<User> example = Example.<User>of(user, NAME_MATCHER);

        if(!repository.exists(example)) {
            repository.save(user);
        }
    }

    private static void setupJob(JobListingRepository repository,
                                 SkillRepository skillRepository,
                                 JobSkillRepository jobSkillRepository,
                                 ElasticSearchSearcherImpl elasticSearchSearcherImpl,
                                 String jobRole,
                                 String jobType,
                                 String company,
                                 String description,
                                 String location,
                                 BigDecimal experience,
                                 String title,
                                 BigDecimal salary,
                                 String jobLink,
                                 List<String> skillList,
                                 Integer recruiterUserId) {

        JobListing jobListing = new JobListing();
        jobListing.setJobRole(jobRole);
        jobListing.setJobType(jobType);
        jobListing.setCompany(company);
        jobListing.setDescription(description);
        jobListing.setLocation(location);
        jobListing.setExperience(experience);
        jobListing.setTitle(title);
        jobListing.setSalary(salary);
        jobListing.setJobLink(jobLink);
        jobListing.setPostedBy(recruiterUserId);

        if (saveJobIfNotExists(repository, jobListing)) {
            saveJobSkills(skillRepository, jobListing, skillList, jobSkillRepository);
            elasticSearchSearcherImpl.upsertJobInElasticSearch(jobListing.getJobId(), jobListing, skillList);
        }
    }

    private static void jobSetup(JobListingRepository repository,
                                 SkillRepository skillRepository,
                                 JobSkillRepository jobSkillRepository,
                                 ElasticSearchSearcherImpl elasticSearchSearcherImpl, Integer recruiterUserId) {

        List<String> skillList1 = List.of("java", "c#", "Java Script", "json", "html", "html5", "css");
        setupJob(repository, skillRepository, jobSkillRepository, elasticSearchSearcherImpl,
                "Frontend Developer", "Full Time", "Intuit", "test", "In-Office",
                BigDecimal.valueOf(0), "Fresher Frontend Developer", BigDecimal.valueOf(1000.00),
                "https://g.co/kgs/6ii1PmY", skillList1, recruiterUserId);

        List<String> skillList2 = List.of("java", "java", "python", "golang");
        setupJob(repository, skillRepository, jobSkillRepository, elasticSearchSearcherImpl,
                "Backend Developer", "Part Time", "Amazon", "test", "Remote",
                BigDecimal.valueOf(5), "Java Experienced Backend Part time Developer",
                BigDecimal.valueOf(10004.00), "https://g.co/kgs/KtGmnpy", skillList2, recruiterUserId);

        List<String> skillList3 = List.of("java");
        setupJob(repository, skillRepository, jobSkillRepository, elasticSearchSearcherImpl,
                "Fullstack Developer", "Contract", "Visa", "test", "In-Office",
                BigDecimal.valueOf(9), "Java Experienced Fullstack contractual Developer",
                BigDecimal.valueOf(10007.00), "https://g.co/kgs/JbHw9ND", skillList3, recruiterUserId);

        List<String> skillList4 = List.of("Java Script", "json", "html", "html5", "css", "team management");
        setupJob(repository, skillRepository, jobSkillRepository, elasticSearchSearcherImpl,
                "Lead Developer", "Full Time", "Meta", "test", "In-Office",
                BigDecimal.valueOf(10), "Lead Developer", BigDecimal.valueOf(20000),
                "https://g.co/kgs/MQ6ucLP", skillList4, recruiterUserId);
    }

    private static void saveJobSkills(SkillRepository skillRepository, JobListing jobListing, List<String> skillList, JobSkillRepository jobSkillRepository) {
        List<Skill> skillSets = skillRepository.findBySkillNameInAndActive(skillList, true);

        saveJobSkill(jobListing, new HashSet<>(skillSets), jobSkillRepository);
    }

    private static void saveJobSkill(JobListing jobListing, Set<Skill> skills, JobSkillRepository jobSkillRepository) {
        for (Skill skill : skills) {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setSkill(skill);
            jobSkill.setJob(jobListing);
            jobSkill.setActive(true); // Set default value
            jobSkillRepository.save(jobSkill);
        }
    }

    private static boolean saveJobIfNotExists(JobListingRepository repository, JobListing jobListing) {
        ExampleMatcher NAME_MATCHER = ExampleMatcher.matching()
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.ignoreCase());
        Example<JobListing> jobListingExample = Example.of(jobListing, NAME_MATCHER);

        if(!repository.exists(jobListingExample)) {
            repository.save(jobListing);
            return true;
        }

        return false;
    }
}
