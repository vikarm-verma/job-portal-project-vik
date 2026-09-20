package com.company.jobportal.service.dashboard;

import com.company.jobportal.dto.application.ApplicationResponse;
import com.company.jobportal.dto.dashboard.RecruiterDashboardResponse;
import com.company.jobportal.dto.job.JobResponse;
import com.company.jobportal.entity.application.Application;
import com.company.jobportal.entity.application.ApplicationStatus;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.user.RecruiterProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.application.ApplicationRepository;
import com.company.jobportal.repository.job.JobRepository;
import com.company.jobportal.repository.user.RecruiterProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RecruiterDashboardService {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public RecruiterDashboardService(UserRepository userRepository,
                                     RecruiterProfileRepository recruiterProfileRepository,
                                     JobRepository jobRepository,
                                     ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs() {
        return jobRepository.findByRecruiterProfile(getCurrentRecruiterProfile(), Pageable.unpaged())
                .getContent().stream()
                .map(this::toJobResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicantsForMyJobs() {
        List<ApplicationResponse> applicants = new ArrayList<>();
        for (Job job : getRecruiterJobs()) {
            applicants.addAll(applicationRepository.findByJob(job).stream().map(this::toResponse).toList());
        }
        return applicants;
    }

    @Transactional(readOnly = true)
    public RecruiterDashboardResponse getDashboardSummary() {
        List<Job> jobs = getRecruiterJobs();
        Map<ApplicationStatus, Long> counts = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            counts.put(status, 0L);
        }
        long totalApplicants = 0;
        for (Job job : jobs) {
            for (Application application : applicationRepository.findByJob(job)) {
                totalApplicants++;
                counts.computeIfPresent(application.getStatus(), (key, value) -> value + 1);
            }
        }
        Map<String, Long> responseCounts = new java.util.LinkedHashMap<>();
        counts.forEach((status, count) -> responseCounts.put(status.name(), count));
        return RecruiterDashboardResponse.builder()
                .totalJobs(jobs.size())
                .totalApplicants(totalApplicants)
                .applicationCountsByStatus(responseCounts)
                .build();
    }

    private RecruiterProfile getCurrentRecruiterProfile() {
        User user = getCurrentUser();
        if (user.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recruiters can access the dashboard");
        }
        return recruiterProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a recruiter profile first"));
    }

    private List<Job> getRecruiterJobs() {
        return jobRepository.findByRecruiterProfile(getCurrentRecruiterProfile(), Pageable.unpaged()).getContent();
    }

    private JobResponse toJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .recruiterProfileId(job.getRecruiterProfile().getId())
                .companyName(job.getRecruiterProfile().getCompanyName())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .experienceLevel(job.getExperienceLevel())
                .requiredSkills(job.getRequiredSkills())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .jobSeekerProfileId(application.getJobSeekerProfile().getId())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
