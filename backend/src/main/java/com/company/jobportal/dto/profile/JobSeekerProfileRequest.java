package com.company.jobportal.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileRequest {
    private String phone;
    private String city;
    private String experienceLevel;
    private String skills;
    private String summary;
    private Boolean profileComplete;
}
