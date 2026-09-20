package com.company.jobportal.dto.dashboard;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Builder
@Value
public class RecruiterDashboardResponse {
    long totalJobs;
    long totalApplicants;
    Map<String, Long> applicationCountsByStatus;
}
