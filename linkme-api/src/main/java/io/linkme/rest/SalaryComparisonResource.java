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

    @GetMapping
    public ResponseEntity<List<SalaryComparisonDTO>> getAllSalaryComparisons() {
        return ResponseEntity.ok(salaryComparisonService.findAll());
    }

    @GetMapping("/{comparisonId}")
    public ResponseEntity<SalaryComparisonDTO> getSalaryComparison(
            @PathVariable(name = "comparisonId") final Integer comparisonId) {
        return ResponseEntity.ok(salaryComparisonService.get(comparisonId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createSalaryComparison(
            @RequestBody @Valid final SalaryComparisonDTO salaryComparisonDTO) {
        final Integer createdComparisonId = salaryComparisonService.create(salaryComparisonDTO);
        return new ResponseEntity<>(createdComparisonId, HttpStatus.CREATED);
    }

    @PutMapping("/{comparisonId}")
    public ResponseEntity<Integer> updateSalaryComparison(
            @PathVariable(name = "comparisonId") final Integer comparisonId,
            @RequestBody @Valid final SalaryComparisonDTO salaryComparisonDTO) {
        salaryComparisonService.update(comparisonId, salaryComparisonDTO);
        return ResponseEntity.ok(comparisonId);
    }

    @DeleteMapping("/{comparisonId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSalaryComparison(
            @PathVariable(name = "comparisonId") final Integer comparisonId) {
        salaryComparisonService.delete(comparisonId);
        return ResponseEntity.noContent().build();
    }

}
