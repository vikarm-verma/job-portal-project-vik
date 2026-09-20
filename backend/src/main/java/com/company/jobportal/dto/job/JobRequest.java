package com.company.jobportal.dto.job;

import com.company.jobportal.entity.job.EmploymentType;
import com.company.jobportal.entity.job.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    @NotBlank(message = "Job title is required")
    private String title;
    @NotBlank(message = "Job description is required")
    private String description;
    @NotBlank(message = "Location is required")
    private String location;
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;
    @NotNull(message = "Salary minimum is required")
    @PositiveOrZero(message = "Salary minimum cannot be negative")
    private Double salaryMin;
    @PositiveOrZero(message = "Salary maximum cannot be negative")
    private Double salaryMax;
    private String experienceLevel;
    @NotBlank(message = "Skills required is required")
    private String requiredSkills;
    private JobStatus status;
}
