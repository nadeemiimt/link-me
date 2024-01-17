package io.linkme.rest;

import io.linkme.model.SalaryComparisonDTO;
import io.linkme.service.user.SalaryComparisonService;
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
@RequestMapping(value = "/api/salaryComparisons", produces = MediaType.APPLICATION_JSON_VALUE)
public class SalaryComparisonResource {

    private final SalaryComparisonService salaryComparisonService;

    public SalaryComparisonResource(final SalaryComparisonService salaryComparisonService) {
        this.salaryComparisonService = salaryComparisonService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<SalaryComparisonDTO> getSalaryComparisonByProfileId(
            @PathVariable(name = "profileId") final Integer profileId) {
        return ResponseEntity.ok(salaryComparisonService.findSalaryComparisonByProfileId(profileId));
    }

}
