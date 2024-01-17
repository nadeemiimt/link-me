package io.linkme.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "SalaryComparisons")
@Getter
@Setter
public class SalaryComparison {

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
    private Integer comparisonId;

    @Column(precision = 20, scale = 2)
    private BigDecimal employeeMidPointSalaryForLocation;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Column(nullable = false)
    private String status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @PrePersist
    protected void onCreate() {
        this.updatedOn = new Date();
    }
}
