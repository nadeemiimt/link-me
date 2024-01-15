package io.linkme.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Skills")
@Getter
@Setter
public class Skill {
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
    private Integer skillId;

    @Column(unique = true)
    private String skillName;

    @Column(columnDefinition = "BIT DEFAULT 1")
    private Boolean active;
}
