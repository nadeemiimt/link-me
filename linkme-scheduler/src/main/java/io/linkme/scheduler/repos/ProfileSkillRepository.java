package io.linkme.scheduler.repos;

import io.linkme.scheduler.domain.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Integer> {

    Set<ProfileSkill> findByProfile_ProfileId(Integer profileId);
}
