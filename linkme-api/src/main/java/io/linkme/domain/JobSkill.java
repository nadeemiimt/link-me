package io.linkme.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "JobSkills")
@Getter
@Setter
public class JobSkill {
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
    private Integer jobSkillId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private JobListing job;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private JobListing profile;

    @Column(columnDefinition = "BIT DEFAULT 1")
    private Boolean active;
}
