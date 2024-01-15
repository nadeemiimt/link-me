package io.linkme.rest;

import io.linkme.model.SkillDTO;
import io.linkme.service.common.SkillServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/skills", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkillResource {

    private final SkillServiceImpl skillServiceImpl;

    public SkillResource(final SkillServiceImpl skillServiceImpl) {
        this.skillServiceImpl = skillServiceImpl;
    }

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllMessages() {
        return ResponseEntity.ok(skillServiceImpl.findAllSkills());
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDTO> getSkill(
            @PathVariable(name = "skillId") final Integer skillId) {
        return ResponseEntity.ok(skillServiceImpl.get(skillId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createSkill(@RequestBody @Valid final SkillDTO skillDTO) {
        final Integer createdSkillId = skillServiceImpl.create(skillDTO);
        return new ResponseEntity<>(createdSkillId, HttpStatus.CREATED);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<Integer> updateSkill(
            @PathVariable(name = "messageId") final Integer skillId,
            @RequestBody @Valid final SkillDTO skillDTO) {
        skillServiceImpl.update(skillId, skillDTO);
        return ResponseEntity.ok(skillId);
    }

    @DeleteMapping("/{skillId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable(name = "skillId") final Integer skillId) {
        skillServiceImpl.delete(skillId);
        return ResponseEntity.noContent().build();
    }

}
