package com.company.jobportal.controller.profile;

import com.company.jobportal.dto.profile.JobSeekerProfileRequest;
import com.company.jobportal.dto.profile.JobSeekerProfileResponse;
import com.company.jobportal.dto.profile.ProfileRequest;
import com.company.jobportal.dto.profile.RecruiterProfileRequest;
import com.company.jobportal.dto.profile.RecruiterProfileResponse;
import com.company.jobportal.dto.resume.ResumeResponse;
import com.company.jobportal.service.profile.UserProfileService;
import com.company.jobportal.service.resume.ResumeUploadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ResumeUploadService resumeUploadService;

    public ProfileController(UserProfileService userProfileService,
                             ResumeUploadService resumeUploadService) {
        this.userProfileService = userProfileService;
        this.resumeUploadService = resumeUploadService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (hasRole(userDetails, "ROLE_JOB_SEEKER")) {
            return ResponseEntity.ok(userProfileService.getMyJobSeekerProfile());
        }
        return ResponseEntity.ok(userProfileService.getMyRecruiterProfile());
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @org.springframework.web.bind.annotation.RequestBody ProfileRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (hasRole(userDetails, "ROLE_JOB_SEEKER")) {
            JobSeekerProfileRequest jobSeekerRequest = JobSeekerProfileRequest.builder()
                    .phone(request.getPhone())
                    .city(request.getCity())
                    .experienceLevel(request.getExperienceLevel())
                    .skills(request.getSkills())
                    .summary(request.getSummary())
                    .profileComplete(request.getProfileComplete())
                    .build();
            return ResponseEntity.ok(userProfileService.createOrUpdateJobSeekerProfile(jobSeekerRequest));
        }
        RecruiterProfileRequest recruiterRequest = RecruiterProfileRequest.builder()
                .companyName(request.getCompanyName())
                .companyWebsite(request.getCompanyWebsite())
                .location(request.getLocation())
                .companyDescription(request.getCompanyDescription())
                .build();
        return ResponseEntity.ok(userProfileService.createOrUpdateRecruiterProfile(recruiterRequest));
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ResumeResponse> uploadResume(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file is required");
        }
        String fileName = file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename();
        String contentType = file.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : file.getContentType();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeUploadService.uploadResume(fileName, fileName, contentType));
    }

    @GetMapping("/resume")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ResumeResponse> getResume() {
        return ResponseEntity.ok(resumeUploadService.getMyResume());
    }

    private boolean hasRole(UserDetails userDetails, String role) {
        return userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
