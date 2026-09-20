package com.company.jobportal.dto.profile;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RecruiterProfileResponse {
    Long id;
    Long userId;
    String companyName;
    String companyWebsite;
    String location;
    String companyDescription;
}
