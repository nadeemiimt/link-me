package io.linkme.scheduler.repos;

import io.linkme.scheduler.domain.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface JobSkillRepository extends JpaRepository<JobSkill, Integer> {
    Set<JobSkill> findByJob_JobId(Integer jobId);
}
