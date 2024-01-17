package io.linkme.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileModel {
    private Integer userId;

    private List<String> skills;

    private BigDecimal workExperience;

    private String location;
    private BigDecimal salary;
    private String currencyCode;
}
