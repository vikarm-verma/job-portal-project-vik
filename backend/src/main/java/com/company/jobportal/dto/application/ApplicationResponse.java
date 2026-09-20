package com.company.jobportal.dto.application;

import com.company.jobportal.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class ApplicationResponse {
    Long id;
    Long jobId;
    String jobTitle;
    Long jobSeekerProfileId;
    ApplicationStatus status;
    LocalDateTime appliedAt;
    LocalDateTime updatedAt;
}
