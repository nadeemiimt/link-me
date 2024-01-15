package io.linkme.service.user;

import io.linkme.domain.JobListing;
import io.linkme.domain.User;
import io.linkme.domain.UserJobApplication;
import io.linkme.model.UserJobApplicationDTO;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.UserJobApplicationRepository;
import io.linkme.repos.UserRepository;
import io.linkme.service.contracts.UserJobApplicationService;
import io.linkme.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserJobApplicationServiceImpl implements UserJobApplicationService {

    private final UserJobApplicationRepository userJobApplicationRepository;
    private final UserRepository userRepository;
    private final JobListingRepository jobListingRepository;

    public UserJobApplicationServiceImpl(
            final UserJobApplicationRepository userJobApplicationRepository,
            final UserRepository userRepository, final JobListingRepository jobListingRepository) {
        this.userJobApplicationRepository = userJobApplicationRepository;
        this.userRepository = userRepository;
        this.jobListingRepository = jobListingRepository;
    }

    /**
     * find all user job applications
     * @return
     */
    @Override
    public List<UserJobApplicationDTO> findAll() {
        final List<UserJobApplication> userJobApplications = userJobApplicationRepository.findAll(Sort.by("applicationId"));
        return userJobApplications.stream()
                .map(userJobApplication -> mapToDTO(userJobApplication, new UserJobApplicationDTO()))
                .toList();
    }

    /**
     * get all user applications
     * @param applicationId
     * @return
     */
    @Override
    public UserJobApplicationDTO get(final Integer applicationId) {
        return userJobApplicationRepository.findById(applicationId)
                .map(userJobApplication -> mapToDTO(userJobApplication, new UserJobApplicationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    /**
     * apply for job
     * @param userJobApplicationDTO
     * @return
     */
    @Override
    public Integer create(final UserJobApplicationDTO userJobApplicationDTO) {
        final UserJobApplication userJobApplication = new UserJobApplication();
        mapToEntity(userJobApplicationDTO, userJobApplication);
        return userJobApplicationRepository.save(userJobApplication).getApplicationId();
    }

    /**
     * update job
     * @param applicationId
     * @param userJobApplicationDTO
     */
    @Override
    public void update(final Integer applicationId,
                       final UserJobApplicationDTO userJobApplicationDTO) {
        final UserJobApplication userJobApplication = userJobApplicationRepository.findById(applicationId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userJobApplicationDTO, userJobApplication);
        userJobApplicationRepository.save(userJobApplication);
    }

    /**
     * delete job application
     * @param applicationId
     */
    @Override
    public void delete(final Integer applicationId) {
        userJobApplicationRepository.deleteById(applicationId);
    }

    private UserJobApplicationDTO mapToDTO(final UserJobApplication userJobApplication,
            final UserJobApplicationDTO userJobApplicationDTO) {
        userJobApplicationDTO.setApplicationId(userJobApplication.getApplicationId());
        userJobApplicationDTO.setApplicationDate(userJobApplication.getApplicationDate());
        userJobApplicationDTO.setStatus(userJobApplication.getStatus());
        userJobApplicationDTO.setUser(userJobApplication.getUser() == null ? null : userJobApplication.getUser().getUserId());
        userJobApplicationDTO.setJob(userJobApplication.getJob() == null ? null : userJobApplication.getJob().getJobId());
        return userJobApplicationDTO;
    }

    private UserJobApplication mapToEntity(final UserJobApplicationDTO userJobApplicationDTO,
            final UserJobApplication userJobApplication) {
        userJobApplication.setApplicationDate(userJobApplicationDTO.getApplicationDate());
        userJobApplication.setStatus(userJobApplicationDTO.getStatus());
        final User user = userJobApplicationDTO.getUser() == null ? null : userRepository.findById(userJobApplicationDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        userJobApplication.setUser(user);
        final JobListing job = userJobApplicationDTO.getJob() == null ? null : jobListingRepository.findById(userJobApplicationDTO.getJob())
                .orElseThrow(() -> new NotFoundException("job not found"));
        userJobApplication.setJob(job);
        return userJobApplication;
    }

}
