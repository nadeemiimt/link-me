package io.linkme.service.user;

import io.linkme.domain.JobListing;
import io.linkme.domain.SalaryComparison;
import io.linkme.model.SalaryComparisonDTO;
import io.linkme.repos.JobListingRepository;
import io.linkme.repos.SalaryComparisonRepository;
import io.linkme.util.NotFoundException;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SalaryComparisonService {

    private final SalaryComparisonRepository salaryComparisonRepository;
    private final JobListingRepository jobListingRepository;

    public SalaryComparisonService(final SalaryComparisonRepository salaryComparisonRepository,
            final JobListingRepository jobListingRepository) {
        this.salaryComparisonRepository = salaryComparisonRepository;
        this.jobListingRepository = jobListingRepository;
    }

    public List<SalaryComparisonDTO> findAll() {
        final List<SalaryComparison> salaryComparisons = salaryComparisonRepository.findAll(Sort.by("comparisonId"));
        return salaryComparisons.stream()
                .map(salaryComparison -> mapToDTO(salaryComparison, new SalaryComparisonDTO()))
                .toList();
    }

    public SalaryComparisonDTO get(final Integer comparisonId) {
        return salaryComparisonRepository.findById(comparisonId)
                .map(salaryComparison -> mapToDTO(salaryComparison, new SalaryComparisonDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SalaryComparisonDTO salaryComparisonDTO) {
        final SalaryComparison salaryComparison = new SalaryComparison();
        mapToEntity(salaryComparisonDTO, salaryComparison);
        return salaryComparisonRepository.save(salaryComparison).getComparisonId();
    }

    public void update(final Integer comparisonId, final SalaryComparisonDTO salaryComparisonDTO) {
        final SalaryComparison salaryComparison = salaryComparisonRepository.findById(comparisonId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(salaryComparisonDTO, salaryComparison);
        salaryComparisonRepository.save(salaryComparison);
    }

    public void delete(final Integer comparisonId) {
        salaryComparisonRepository.deleteById(comparisonId);
    }

    private SalaryComparisonDTO mapToDTO(final SalaryComparison salaryComparison,
            final SalaryComparisonDTO salaryComparisonDTO) {
        salaryComparisonDTO.setComparisonId(salaryComparison.getComparisonId());
        salaryComparisonDTO.setSalaryAmount(salaryComparison.getSalaryAmount());
        salaryComparisonDTO.setLocation(salaryComparison.getLocation());
        salaryComparisonDTO.setTimestamp(salaryComparison.getTimestamp());
        salaryComparisonDTO.setJob(salaryComparison.getJob() == null ? null : salaryComparison.getJob().getJobId());
        return salaryComparisonDTO;
    }

    private SalaryComparison mapToEntity(final SalaryComparisonDTO salaryComparisonDTO,
            final SalaryComparison salaryComparison) {
        salaryComparison.setSalaryAmount(salaryComparisonDTO.getSalaryAmount());
        salaryComparison.setLocation(salaryComparisonDTO.getLocation());
        salaryComparison.setTimestamp(salaryComparisonDTO.getTimestamp());
        final JobListing job = salaryComparisonDTO.getJob() == null ? null : jobListingRepository.findById(salaryComparisonDTO.getJob())
                .orElseThrow(() -> new NotFoundException("job not found"));
        salaryComparison.setJob(job);
        return salaryComparison;
    }

}
