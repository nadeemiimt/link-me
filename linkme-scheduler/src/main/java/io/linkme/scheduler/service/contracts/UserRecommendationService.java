package io.linkme.scheduler.service.contracts;

import io.linkme.scheduler.model.ProfileInfo;

import java.io.IOException;

public interface UserRecommendationService {
    void findEachActiveUserAndTheirRecommendedJobs();

    void findMatchJobForUserProfile(ProfileInfo profileInfo) throws IOException;
}
