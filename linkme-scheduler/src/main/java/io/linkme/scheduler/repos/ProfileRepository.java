package io.linkme.scheduler.repos;

import io.linkme.scheduler.domain.Profile;
import io.linkme.scheduler.model.ProfileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByEmailAndActive(String email, Boolean active);

    @Query("SELECT NEW io.linkme.scheduler.model.ProfileInfo(p.email, p.firstName, p.lastName, p.skills) FROM Profile p WHERE p.skills IS NOT EMPTY AND p.active = true")
    List<ProfileInfo> findUserEmailNameAndSkillsWithNonNullProfile();
}
