package io.linkme.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SalaryComparisonDTO {

    private Integer comparisonId;
    private Integer profileId;
    private BigDecimal salaryAmount;
    private String location;
    private String status;
    private Date updatedOn;
    private BigDecimal employeeMidPointSalaryForLocation;

}
