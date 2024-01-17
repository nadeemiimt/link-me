package io.linkme.repos;

import io.linkme.domain.SalaryComparison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SalaryComparisonRepository extends JpaRepository<SalaryComparison, Integer> {

    // Repository method to find SalaryComparison data by profile ID
    Optional<SalaryComparison> findByProfile_ProfileId(Integer profileId);
}
