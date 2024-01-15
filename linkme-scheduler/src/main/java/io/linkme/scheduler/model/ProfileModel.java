package io.linkme.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileModel {
    private Integer userId;

    private List<String> skills;

    private String workExperience;

    private String location;
}
