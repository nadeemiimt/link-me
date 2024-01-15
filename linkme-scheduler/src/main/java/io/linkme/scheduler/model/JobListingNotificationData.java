package io.linkme.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobListingNotificationData {
    private Integer jobId;
    private String jobTitle;
    private String jobLink;
}
