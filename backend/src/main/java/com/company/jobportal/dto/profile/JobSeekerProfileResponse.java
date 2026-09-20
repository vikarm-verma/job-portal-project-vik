package com.company.jobportal.dto.profile;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class JobSeekerProfileResponse {
    Long id;
    Long userId;
    String phone;
    String city;
    String experienceLevel;
    String skills;
    String summary;
    boolean profileComplete;
}
