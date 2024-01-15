package io.linkme.repos;

import io.linkme.domain.SalaryComparison;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SalaryComparisonRepository extends JpaRepository<SalaryComparison, Integer> {
}
