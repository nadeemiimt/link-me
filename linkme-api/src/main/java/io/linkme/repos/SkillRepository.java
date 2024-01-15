package io.linkme.repos;

import io.linkme.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SkillRepository extends JpaRepository<Skill, Integer> {
    List<Skill> findBySkillNameInAndActive(List<String> skillNames, Boolean active);

    List<Skill> findByActiveTrue();

}
