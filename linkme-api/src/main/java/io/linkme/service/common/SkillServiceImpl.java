package io.linkme.service.common;

import io.linkme.domain.Skill;
import io.linkme.model.SkillDTO;
import io.linkme.repos.SkillRepository;
import io.linkme.service.contracts.SkillService;
import io.linkme.util.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(final SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * find all stored skills for dropdown
     * @return
     */
    @Override
    public List<SkillDTO> findAllSkills() {
        final List<Skill> skills = skillRepository.findByActiveTrue();
        return skills.stream()
                .map(skill -> mapToDTO(skill, new SkillDTO()))
                .toList();
    }

    /**
     * find skill by skill name
     * @param skillNames
     * @return
     */
    @Override
    public List<SkillDTO> findAllSkillsBySkillNames(List<String> skillNames) {
        final List<Skill> skills = skillRepository.findBySkillNameInAndActive(skillNames, true);
        return skills.stream()
                .map(skill -> mapToDTO(skill, new SkillDTO()))
                .toList();
    }

    /**
     * get skill by skill id
     * @param skillId
     * @return
     */
    @Override
    public SkillDTO get(final Integer skillId) {
        return skillRepository.findById(skillId)
                .map(skill -> mapToDTO(skill, new SkillDTO()))
                .orElseThrow(NotFoundException::new);
    }

    /**
     * insert skill if not exists
     * @param skillNames
     * @param active
     * @return
     */
    @Override
    @Transactional
    public List<Integer> insertSkillsIfNotExist(List<String> skillNames, Boolean active) {
        List<Skill> existingSkills = skillRepository.findBySkillNameInAndActive(skillNames, active);

        // Insert only non-existing skills
        List<Skill> skillsToInsert = skillNames.stream()
                .filter(skillName -> existingSkills.stream().noneMatch(skill -> skill.getSkillName().equals(skillName)))
                .map(skillName -> {
                    Skill newSkill = new Skill();
                    newSkill.setSkillName(skillName);
                    newSkill.setActive(active);
                    return newSkill;
                })
                .collect(Collectors.toList());

        List<Skill> insertedSkills = skillRepository.saveAll(skillsToInsert);

        // Return the IDs of the inserted skills
        return insertedSkills.stream().map(Skill::getSkillId).collect(Collectors.toList());
    }

    /**
     * create skill
     * @param skillDTO
     * @return
     */
    @Override
    public Integer create(final SkillDTO skillDTO) {
        return this.insertSkillsIfNotExist(List.of(skillDTO.getSkillName()), skillDTO.getActive()).get(0);
    }

    /**
     * create list of skills
     * @param skills
     * @param active
     * @return
     */
    @Override
    public List<Integer> createListOfSkills(final List<Skill> skills, Boolean active) {
        try {
            List<Integer> skillIds = this.insertSkillsIfNotExist(skills.stream().map(x -> x.getSkillName()).collect(Collectors.toList()), true);
            return skillIds;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * update skill
     * @param skillId
     * @param skillDTO
     */
    @Override
    public void update(final Integer skillId, final SkillDTO skillDTO) {
        final Skill skill = skillRepository.findById(skillId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(skillDTO, skill);
        skillRepository.save(skill);
    }

    /**
     * delete skill
     * @param skillId
     */
    @Override
    public void delete(final Integer skillId) {
        skillRepository.deleteById(skillId);
    }

    private SkillDTO mapToDTO(final Skill skill, final SkillDTO skillDTO) {
        skillDTO.setSkillId(skill.getSkillId());
        skillDTO.setSkillName(skill.getSkillName());
        skillDTO.setActive(skill.getActive());
        return skillDTO;
    }

    private Skill mapToEntity(final SkillDTO skillDTO, final Skill skill) {
        skill.setSkillId(skillDTO.getSkillId());
        skill.setSkillName(skillDTO.getSkillName());
        skill.setActive(skillDTO.getActive());
        return skill;
    }

}
