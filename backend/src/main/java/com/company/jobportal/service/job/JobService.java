package com.company.jobportal.service.job;

import com.company.jobportal.dto.job.JobRequest;
import com.company.jobportal.dto.job.JobResponse;
import com.company.jobportal.entity.job.EmploymentType;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.job.JobStatus;
import com.company.jobportal.entity.user.RecruiterProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.job.JobRepository;
import com.company.jobportal.repository.user.RecruiterProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobService {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;

    public JobService(UserRepository userRepository,
                      RecruiterProfileRepository recruiterProfileRepository,
                      JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        RecruiterProfile recruiter = getCurrentRecruiterProfile();
        validateJobRequest(request);
        Job job = Job.builder().recruiterProfile(recruiter).build();
        applyChanges(job, request, true);
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request) {
        RecruiterProfile recruiter = getCurrentRecruiterProfile();
        Job job = getJob(jobId);
        requireJobOwner(job, recruiter);
        validateJobRequest(request);
        applyChanges(job, request, false);
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long jobId) {
        RecruiterProfile recruiter = getCurrentRecruiterProfile();
        Job job = getJob(jobId);
        requireJobOwner(job, recruiter);
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long jobId) {
        return toResponse(getJob(jobId));
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(String keyword, String location,
                                        EmploymentType employmentType, Pageable pageable) {
        String searchTerm = keyword == null ? null : keyword.trim();
        String locationTerm = location == null ? null : location.trim();
        return jobRepository.search(searchTerm, locationTerm, employmentType, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(Pageable pageable) {
        return jobRepository.findByRecruiterProfile(getCurrentRecruiterProfile(), pageable).map(this::toResponse);
    }

    private void applyChanges(Job job, JobRequest request, boolean creating) {
        job.setTitle(request.getTitle().trim());
        job.setDescription(request.getDescription().trim());
        job.setLocation(request.getLocation().trim());
        job.setEmploymentType(request.getEmploymentType());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setRequiredSkills(request.getRequiredSkills().trim());
        if (creating || request.getStatus() != null) {
            job.setStatus(request.getStatus() == null ? JobStatus.OPEN : request.getStatus());
        }
    }

    private void validateJobRequest(JobRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()
                || request.getLocation() == null || request.getLocation().isBlank()
                || request.getEmploymentType() == null || request.getSalaryMin() == null
                || request.getRequiredSkills() == null || request.getRequiredSkills().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required job fields are missing");
        }
        if (request.getSalaryMax() != null && request.getSalaryMax() < request.getSalaryMin()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum salary cannot be less than minimum salary");
        }
    }

    private Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    private RecruiterProfile getCurrentRecruiterProfile() {
        User user = getCurrentUser();
        if (user.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recruiters can manage jobs");
        }
        return recruiterProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a recruiter profile first"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private void requireJobOwner(Job job, RecruiterProfile recruiter) {
        if (!job.getRecruiterProfile().getId().equals(recruiter.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own jobs");
        }
    }

    private JobResponse toResponse(Job job) {
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
}
