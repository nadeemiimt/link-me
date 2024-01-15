package io.linkme.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SalaryComparisonDTO {

    private Integer comparisonId;

    @Digits(integer = 20, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "70.08")
    private BigDecimal salaryAmount;

    @Size(max = 100)
    private String location;

    private OffsetDateTime timestamp;

    private Integer job;

}
