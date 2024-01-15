package io.linkme.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationData {
    private String email;
    private String userFullName;
    private List<JobListingNotificationData> jobListingNotificationDataList;
}
