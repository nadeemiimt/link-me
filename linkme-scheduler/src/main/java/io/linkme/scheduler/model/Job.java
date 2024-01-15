package io.linkme.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    private int jobId;
    private List<String> skills;
    @Size(max = 100)
    private String jobRole;

    @Size(max = 100)
    private String jobType;

    @Digits(integer = 20, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "90.08")
    private BigDecimal experience;

    @Size(max = 100)
    private String location;
}
