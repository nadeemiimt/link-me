package io.linkme.scheduler.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {

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
    private Integer userId;

    @Column(nullable = false, columnDefinition = "BIT")
    private Boolean recruiter;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "varchar(max)")
    private String skills;

    @Column(columnDefinition = "varchar(max)")
    private String education;

    @Column(columnDefinition = "varchar(max)")
    private String experience;

    @Column(columnDefinition = "varchar(max)")
    private String location;

    @Column(columnDefinition = "varchar(max)")
    private String otherProfileDetails;

    @OneToMany(mappedBy = "user")
    private Set<UserJobApplication> userUserJobApplications;

    @OneToMany(mappedBy = "sender")
    private Set<Message> senderMessages;

    @OneToMany(mappedBy = "receiver")
    private Set<Message> receiverMessages;

}
