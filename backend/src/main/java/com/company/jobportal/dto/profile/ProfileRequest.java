package com.company.jobportal.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    @Size(max = 30)
    private String phone;
    @Size(max = 100)
    private String city;
    @Size(max = 100)
    private String experienceLevel;
    @Size(max = 200)
    private String skills;
    private String summary;
    private Boolean profileComplete;
    @Size(max = 150)
    private String companyName;
    @Size(max = 255)
    private String companyWebsite;
    @Size(max = 100)
    private String location;
    private String companyDescription;
}
