package com.company.jobportal.dto.job;

import com.company.jobportal.entity.job.EmploymentType;
import com.company.jobportal.entity.job.JobStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class JobResponse {
    Long id;
    Long recruiterProfileId;
    String companyName;
    String title;
    String description;
    String location;
    EmploymentType employmentType;
    Double salaryMin;
    Double salaryMax;
    String experienceLevel;
    String requiredSkills;
    JobStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
