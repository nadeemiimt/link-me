package io.linkme.service.user;

import io.linkme.domain.Profile;
import io.linkme.domain.ProfileSkill;
import io.linkme.domain.Skill;
import io.linkme.model.ProfileDTO;
import io.linkme.model.SkillDTO;
import io.linkme.repos.ProfileRepository;
import io.linkme.repos.ProfileSkillRepository;
import io.linkme.service.contracts.FileIoService;
import io.linkme.service.contracts.ProfileService;
import io.linkme.service.contracts.SkillService;
import io.linkme.service.kafka.KafkaProducerService;
import io.linkme.util.NotFoundException;
import io.linkme.model.ProfileModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProfileServiceImpl implements ProfileService {
    private final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillService skillService;
    private final FileIoService fileIoService;

    private final KafkaProducerService kafkaProducerService;

    @Value("${topic.candidate.name}")
    private String candidateTopicName;

    public ProfileServiceImpl(final ProfileRepository profileRepository,
                              final ProfileSkillRepository profileSkillRepository,
                              final FileIoService fileIoService,
                              final SkillService skillService,
                              final KafkaProducerService kafkaProducerService) {
        this.fileIoService = fileIoService;
        this.profileRepository = profileRepository;
        this.profileSkillRepository = profileSkillRepository;
        this.skillService = skillService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public ProfileDTO getByEmail(final String email) {
        Optional<Profile> profile =  profileRepository.findByEmailAndActive(email, true);

        // Fetch JobSkills for each JobListing
        if (profile.isPresent()) {
            Set<ProfileSkill> profileSkills = profileSkillRepository.findByProfile_ProfileId(profile.get().getProfileId());
            profile.get().setSkills(profileSkills);
        }
        return profile.map(jl -> mapToDTO(jl, new ProfileDTO()))
                .orElse(null);
    }

    @Override
    public ProfileDTO get(final Integer profileId) {
        return profileRepository.findById(profileId)
                .map(profile -> mapToDTO(profile, new ProfileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Integer create(final ProfileDTO profileDTO) {
        final Profile profile = new Profile();
        mapToEntity(profileDTO, profile);
        Integer profileId = profileRepository.save(profile).getProfileId();
        profileDTO.setProfileId(profileId);

        saveSkills(profileDTO, profile);

        sendToKafka(profileDTO, profileDTO.getUserId());

        return profileId;
    }

    @Override
    public void update(final Integer profileId, final ProfileDTO profileDTO) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(profileDTO, profile);
        // update skills as a separate
        profileRepository.save(profile);

        saveSkills(profileDTO, profile);


        sendToKafka(profileDTO, profileDTO.getUserId());
    }

    @Override
    public void upsert(final ProfileDTO profileDTO) {

        try {
            ProfileDTO dbProfile = this.getByEmail(profileDTO.getEmail());

            if (dbProfile == null) {
                this.create(profileDTO);
            } else {
                this.update(dbProfile.getProfileId(), profileDTO);
            }
        } catch (Exception ex) {
            LOGGER.error("error while updating profile. Details {}", ex);
        }
    }

    @Override
    public void delete(final Integer profileId) {
        profileRepository.deleteById(profileId);
    }


    private void sendToKafka(ProfileModel profileModel, Integer userId) {
        try {
            LOGGER.info("sending message to kafka");
            kafkaProducerService.send(candidateTopicName, String.valueOf(userId), profileModel);
        } catch (Exception ex) {
            LOGGER.error("Send to kafka error. Details {}", ex);
        }
    }

    private void saveSkills(ProfileDTO profileDTO, Profile profile) {
        if(!CollectionUtils.isEmpty(profileDTO.getSkills())) {

            // Save Skills and JobSkills
            Set<ProfileSkill> profileSkills = new HashSet<>();

            List<String> skills = new ArrayList<>();

            profileDTO.getSkills().forEach(skillName -> {
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

            saveUserProfileSkill(profile, skillDataList);

            profile.setSkills(profileSkills);
        }
    }

    private void saveUserProfileSkill(Profile profile, Set<Skill> skills) {
        for (Skill skill : skills) {
            ProfileSkill profileSkill = new ProfileSkill();
            profileSkill.setSkill(skill);
            profileSkill.setProfile(profile);
            profileSkill.setActive(true); // Set default value
            profileSkillRepository.save(profileSkill);
        }
    }


    @Override
    public String uploadFileAndGetUrl(Path path){
        // Use the File.io API to upload the file and get the URL
        return fileIoService.uploadFile(path.toFile());
    }

    private ProfileDTO mapToDTO(final Profile profile, final ProfileDTO profileDTO) {
        profileDTO.setProfileId(profile.getProfileId());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setFirstName(profile.getFirstName());
        profileDTO.setLastName(profile.getLastName());
        profileDTO.setSkills(getSkills(profile));
        profileDTO.setEducation(profile.getEducation());
        profileDTO.setWorkExperience(profile.getWorkExperience());
        profileDTO.setFileUrl(profile.getFileUrl());
        return profileDTO;
    }

    private List<String> getSkills(Profile profile) {
        List<String> result = null;

        if(!CollectionUtils.isEmpty(profile.getSkills())) {
            result = profile.getSkills().stream().map(x -> x.getSkill()).map(x -> x.getSkillName()).collect(Collectors.toList());
        }

        return result;
    }

    private Profile mapToEntity(final ProfileDTO profileDTO, final Profile profile) {
        profile.setProfileId(profileDTO.getProfileId());
        profile.setEmail(profileDTO.getEmail());
        profile.setFirstName(profileDTO.getFirstName());
        profile.setLastName(profileDTO.getLastName());
        profile.setEducation(profileDTO.getEducation());
        profile.setWorkExperience(profileDTO.getWorkExperience());
        profile.setFileUrl(profileDTO.getFileUrl());
        profile.setLocation(profileDTO.getLocation());
        profile.setSalary(profileDTO.getSalary());
        profile.setCurrencyCode(profileDTO.getCurrencyCode());

        if(profileDTO.getActive() == null) {
            profile.setActive(true);
        } else {
            profile.setActive(profileDTO.getActive());
        }
        return profile;
    }

}
