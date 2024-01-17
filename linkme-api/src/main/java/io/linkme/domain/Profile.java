package io.linkme.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "Profiles")
@Getter
@Setter
public class Profile {

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
    private Integer profileId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private BigDecimal workExperience;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String fileUrl;

    @Column
    private BigDecimal salary;

    @Column
    private String currencyCode;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "profile")
    private Set<ProfileSkill> skills;

    @OneToOne(mappedBy = "profile")
    private SalaryComparison jobSalaryComparison;
}
