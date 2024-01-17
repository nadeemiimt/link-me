package io.linkme.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "JobListings")
@Getter
@Setter
public class JobListing {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer jobId;

    @Column
    private String title;

    @Column
    private String company;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 100)
    private String jobRole;

    @Column(length = 100)
    private String jobType;

    @Column(precision = 20, scale = 2)
    private BigDecimal experience;

    @Column(length = 100)
    private String location;

    @Column(precision = 20, scale = 2)
    private BigDecimal salary;

    @Column(columnDefinition = "text")
    private String requirements;

    @Column(columnDefinition = "text")
    private String otherJobDetails;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedOn;

    @Column(nullable = false)
    private Integer postedBy;

    @Column
    private String jobLink;

    @OneToMany(mappedBy = "job")
    private Set<JobSkill> skills;

    @OneToMany(mappedBy = "job")
    private Set<UserJobApplication> jobUserJobApplications;

    @PrePersist
    protected void onCreate() {
        this.postedOn = new Date();
    }
}
