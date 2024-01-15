package io.linkme.service.jobList;

import io.linkme.domain.JobListing;
import io.linkme.domain.User;
import io.linkme.model.JobListingDTO;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.JobSkillRepository;
import io.linkme.repos.UserJobApplicationRepository;
import io.linkme.repos.UserRepository;
import io.linkme.service.contracts.JobListingService;
import io.linkme.service.contracts.SkillService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class JobListingServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobListingRepository jobListingRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private JobSkillRepository jobSkillRepository;
    @Mock
    private UserJobApplicationRepository userJobApplicationRepository;
    @Mock
    private JobListingService jobListingService;

    @Test
    @Disabled
    void testFindAll() {
        // Mock data
        List<JobListing> jobListings = new ArrayList<>();
        jobListings.add(new JobListing(/* provide necessary parameters */));
        jobListings.add(new JobListing(/* provide necessary parameters */));

        // Mock UserRepository response
        when(userRepository.findByEmail(anyString())).thenReturn(new User());

        // Mock JobListingRepository response
        Pageable pageable = PageRequest.of(0, 10);
        when(jobListingRepository.findAll(pageable)).thenReturn(new PageImpl<>(jobListings));

        // Mock SkillService response
        when(skillService.findAllSkillsBySkillNames(anyList())).thenReturn(new ArrayList<>());

        // Mock JobSkillRepository response
        when(jobSkillRepository.findByJob_JobId(anyInt())).thenReturn(new HashSet<>());

        // Mock UserJobApplicationRepository response
        when(userJobApplicationRepository.findByUserAndJob(any(), any())).thenReturn(Optional.empty());

        // Call the method to test
        Page<JobListingDTO> result = jobListingService.findAll(0, 10);

        // Assertions
        assertEquals(2, result.getTotalElements());  // Assuming 2 job listings are returned
    }
}