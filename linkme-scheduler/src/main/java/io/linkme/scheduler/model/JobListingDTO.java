package io.linkme.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.*;


@Data
public class JobListingDTO extends Job {

    public JobListingDTO(){

    }

    @Builder
    public JobListingDTO(int jobId, List<String> skills, @Size(max = 100) String jobRole, @Size(max = 100) String jobType, @Digits(integer = 20, fraction = 2) BigDecimal experience, @Size(max = 100) String location, String title, String company, String description, BigDecimal salary, String requirements, String otherJobDetails, Date postedOn, String jobLink, Integer postedBy, String applicationStatus) {
        super(jobId, skills, jobRole, jobType, experience, location);
        this.title = title;
        this.company = company;
        this.description = description;
        this.salary = salary;
        this.requirements = requirements;
        this.otherJobDetails = otherJobDetails;
        this.postedOn = postedOn;
        this.jobLink = jobLink;
        this.postedBy = postedBy;
        this.applicationStatus = applicationStatus;
    }

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String company;

    private String description;

    @Digits(integer = 20, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "90.08")
    private BigDecimal salary;

    private String requirements;

    private String otherJobDetails;

    private Date postedOn;

    private String jobLink;

    private Integer postedBy;

    private String applicationStatus;

}
