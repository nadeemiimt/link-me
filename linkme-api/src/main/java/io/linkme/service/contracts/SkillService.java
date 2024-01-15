package io.linkme.service.contracts;

import io.linkme.domain.Skill;
import io.linkme.model.SkillDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SkillService {
    List<SkillDTO> findAllSkills();

    List<SkillDTO> findAllSkillsBySkillNames(List<String> skillNames);

    SkillDTO get(Integer skillId);

    @Transactional
    List<Integer> insertSkillsIfNotExist(List<String> skillNames, Boolean active);

    Integer create(SkillDTO skillDTO);

    List<Integer> createListOfSkills(List<Skill> skills, Boolean active);

    void update(Integer skillId, SkillDTO skillDTO);

    void delete(Integer skillId);
}
