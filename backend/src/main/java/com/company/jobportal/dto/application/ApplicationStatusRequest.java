package com.company.jobportal.dto.application;

import com.company.jobportal.entity.application.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusRequest {
    @NotNull(message = "Application status is required")
    private ApplicationStatus status;
}
