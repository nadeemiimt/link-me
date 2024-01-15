package io.linkme.rest;

import io.linkme.model.UserJobApplicationDTO;
import io.linkme.service.user.UserJobApplicationServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/userJobApplications", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserJobApplicationResource {

    private final UserJobApplicationServiceImpl userJobApplicationServiceImpl;

    public UserJobApplicationResource(final UserJobApplicationServiceImpl userJobApplicationServiceImpl) {
        this.userJobApplicationServiceImpl = userJobApplicationServiceImpl;
    }

    @GetMapping
    public ResponseEntity<List<UserJobApplicationDTO>> getAllUserJobApplications() {
        return ResponseEntity.ok(userJobApplicationServiceImpl.findAll());
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<UserJobApplicationDTO> getUserJobApplication(
            @PathVariable(name = "applicationId") final Integer applicationId) {
        return ResponseEntity.ok(userJobApplicationServiceImpl.get(applicationId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createUserJobApplication(
            @RequestBody @Valid final UserJobApplicationDTO userJobApplicationDTO) {
        final Integer createdApplicationId = userJobApplicationServiceImpl.create(userJobApplicationDTO);
        return new ResponseEntity<>(createdApplicationId, HttpStatus.CREATED);
    }

    @PutMapping("/{applicationId}")
    public ResponseEntity<Integer> updateUserJobApplication(
            @PathVariable(name = "applicationId") final Integer applicationId,
            @RequestBody @Valid final UserJobApplicationDTO userJobApplicationDTO) {
        userJobApplicationServiceImpl.update(applicationId, userJobApplicationDTO);
        return ResponseEntity.ok(applicationId);
    }

    @DeleteMapping("/{applicationId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUserJobApplication(
            @PathVariable(name = "applicationId") final Integer applicationId) {
        userJobApplicationServiceImpl.delete(applicationId);
        return ResponseEntity.noContent().build();
    }

}
