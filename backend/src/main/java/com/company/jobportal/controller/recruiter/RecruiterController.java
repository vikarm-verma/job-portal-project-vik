package com.company.jobportal.controller.recruiter;

import com.company.jobportal.dto.application.ApplicationResponse;
import com.company.jobportal.dto.dashboard.RecruiterDashboardResponse;
import com.company.jobportal.dto.job.JobResponse;
import com.company.jobportal.service.application.ApplicationService;
import com.company.jobportal.service.dashboard.RecruiterDashboardService;
import com.company.jobportal.service.job.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterController {

    private final JobService jobService;
    private final RecruiterDashboardService recruiterDashboardService;
    private final ApplicationService applicationService;

    public RecruiterController(JobService jobService,
                               RecruiterDashboardService recruiterDashboardService,
                               ApplicationService applicationService) {
        this.jobService = jobService;
        this.recruiterDashboardService = recruiterDashboardService;
        this.applicationService = applicationService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobResponse>> getMyJobs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(jobService.getMyJobs(pageable));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RecruiterDashboardResponse> getDashboard() {
        return ResponseEntity.ok(recruiterDashboardService.getDashboardSummary());
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsForRecruiterJob(jobId));
    }
}
