package io.linkme.repos;

import io.linkme.domain.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Integer> {

    Set<ProfileSkill> findByProfile_ProfileId(Integer profileId);
}
