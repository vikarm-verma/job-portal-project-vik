package com.company.jobportal.service.application;

import com.company.jobportal.dto.application.ApplicationResponse;
import com.company.jobportal.entity.application.Application;
import com.company.jobportal.entity.application.ApplicationStatus;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.user.JobSeekerProfile;
import com.company.jobportal.entity.user.RecruiterProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.application.ApplicationRepository;
import com.company.jobportal.repository.job.JobRepository;
import com.company.jobportal.repository.user.JobSeekerProfileRepository;
import com.company.jobportal.repository.user.RecruiterProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ApplicationService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public ApplicationService(UserRepository userRepository,
                              JobRepository jobRepository,
                              ApplicationRepository applicationRepository,
                              JobSeekerProfileRepository jobSeekerProfileRepository,
                              RecruiterProfileRepository recruiterProfileRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    @Transactional
    public ApplicationResponse applyForJob(Long jobId) {
        User user = getCurrentUserWithRole(Role.JOB_SEEKER);
        JobSeekerProfile profile = getJobSeekerProfile(user);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (applicationRepository.existsByJobAndJobSeekerProfile(job, profile)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied for this job");
        }
        Application application = Application.builder()
                .job(job)
                .jobSeekerProfile(profile)
                .status(ApplicationStatus.APPLIED)
                .build();
        return toResponse(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications() {
        JobSeekerProfile profile = getJobSeekerProfile(getCurrentUserWithRole(Role.JOB_SEEKER));
        return applicationRepository.findByJobSeekerProfile(profile).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForRecruiterJob(Long jobId) {
        RecruiterProfile recruiter = getRecruiterProfile(getCurrentUserWithRole(Role.RECRUITER));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (!job.getRecruiterProfile().getId().equals(recruiter.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view applications for your own jobs");
        }
        return applicationRepository.findByJob(job).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application status is required");
        }
        RecruiterProfile recruiter = getRecruiterProfile(getCurrentUserWithRole(Role.RECRUITER));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (!application.getJob().getRecruiterProfile().getId().equals(recruiter.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update applications for your own jobs");
        }
        application.setStatus(status);
        return toResponse(applicationRepository.save(application));
    }

    private User getCurrentUserWithRole(Role role) {
        User user = getCurrentUser();
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This operation requires the " + role + " role");
        }
        return user;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private JobSeekerProfile getJobSeekerProfile(User user) {
        return jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a job seeker profile first"));
    }

    private RecruiterProfile getRecruiterProfile(User user) {
        return recruiterProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a recruiter profile first"));
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
