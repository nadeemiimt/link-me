package io.linkme.model;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserJobApplicationDTO {

    private Integer applicationId;

    private OffsetDateTime applicationDate;

    @Size(max = 50)
    private String status;

    private Integer user;

    private Integer job;

}
