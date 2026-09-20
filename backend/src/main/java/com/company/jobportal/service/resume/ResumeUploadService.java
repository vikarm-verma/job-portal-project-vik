package com.company.jobportal.service.resume;

import com.company.jobportal.dto.resume.ResumeResponse;
import com.company.jobportal.entity.resume.Resume;
import com.company.jobportal.entity.user.JobSeekerProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.resume.ResumeRepository;
import com.company.jobportal.repository.user.JobSeekerProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeUploadService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final ResumeRepository resumeRepository;

    public ResumeUploadService(UserRepository userRepository,
                               JobSeekerProfileRepository jobSeekerProfileRepository,
                               ResumeRepository resumeRepository) {
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.resumeRepository = resumeRepository;
    }

    @Transactional
    public ResumeResponse uploadResume(String fileName, String filePath, String contentType) {
        User user = requireCurrentJobSeeker();
        JobSeekerProfile profile = getProfile(user);
        Resume resume = resumeRepository.findByJobSeekerProfile(profile)
                .orElseGet(() -> Resume.builder().jobSeekerProfile(profile).build());
        resume.setFileName(requireText(fileName, "Resume file name is required"));
        resume.setFilePath(requireText(filePath, "Resume file path is required"));
        resume.setContentType(requireText(contentType, "Resume content type is required"));
        return toResponse(resumeRepository.save(resume));
    }

    @Transactional
    public ResumeResponse replaceResume(String fileName, String filePath, String contentType) {
        return uploadResume(fileName, filePath, contentType);
    }

    @Transactional(readOnly = true)
    public ResumeResponse getMyResume() {
        User user = requireCurrentJobSeeker();
        return toResponse(resumeRepository.findByJobSeekerProfile(getProfile(user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found")));
    }

    private User requireCurrentJobSeeker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        if (user.getRole() != Role.JOB_SEEKER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only job seekers can manage resumes");
        }
        return user;
    }

    private JobSeekerProfile getProfile(User user) {
        return jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create a job seeker profile first"));
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return value;
    }

    private ResumeResponse toResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .jobSeekerProfileId(resume.getJobSeekerProfile().getId())
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .contentType(resume.getContentType())
                .uploadedAt(resume.getUploadedAt())
                .build();
    }
}
