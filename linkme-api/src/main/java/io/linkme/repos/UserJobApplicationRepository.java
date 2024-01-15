package io.linkme.repos;

import io.linkme.domain.JobListing;
import io.linkme.domain.User;
import io.linkme.domain.UserJobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserJobApplicationRepository extends JpaRepository<UserJobApplication, Integer> {
    Optional<UserJobApplication> findByUserAndJob(User user, JobListing jobListing);
}
