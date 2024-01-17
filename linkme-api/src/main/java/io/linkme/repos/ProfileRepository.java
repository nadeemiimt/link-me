package io.linkme.repos;

import io.linkme.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByEmailAndActive(String email, Boolean active);
    Optional<Profile> findByProfileIdAndActive(Integer profileId, Boolean active);
}
