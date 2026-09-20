package com.company.jobportal.service.profile;

import com.company.jobportal.dto.profile.JobSeekerProfileRequest;
import com.company.jobportal.dto.profile.JobSeekerProfileResponse;
import com.company.jobportal.dto.profile.RecruiterProfileRequest;
import com.company.jobportal.dto.profile.RecruiterProfileResponse;
import com.company.jobportal.entity.user.JobSeekerProfile;
import com.company.jobportal.entity.user.RecruiterProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.user.JobSeekerProfileRepository;
import com.company.jobportal.repository.user.RecruiterProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public UserProfileService(UserRepository userRepository,
                              JobSeekerProfileRepository jobSeekerProfileRepository,
                              RecruiterProfileRepository recruiterProfileRepository) {
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    @Transactional
    public JobSeekerProfileResponse createOrUpdateJobSeekerProfile(JobSeekerProfileRequest request) {
        User user = requireCurrentUserWithRole(Role.JOB_SEEKER);
        JobSeekerProfile profile = jobSeekerProfileRepository.findByUser(user)
                .orElseGet(() -> JobSeekerProfile.builder().user(user).build());
        applyJobSeekerChanges(profile, request);
        return toResponse(jobSeekerProfileRepository.save(profile));
    }

    @Transactional
    public JobSeekerProfileResponse updateJobSeekerProfile(Long profileId, JobSeekerProfileRequest request) {
        User user = requireCurrentUserWithRole(Role.JOB_SEEKER);
        JobSeekerProfile profile = jobSeekerProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job seeker profile not found"));
        requireOwner(profile.getUser(), user, "You can only update your own profile");
        applyJobSeekerChanges(profile, request);
        return toResponse(jobSeekerProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public JobSeekerProfileResponse getMyJobSeekerProfile() {
        User user = requireCurrentUserWithRole(Role.JOB_SEEKER);
        return toResponse(jobSeekerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job seeker profile not found")));
    }

    @Transactional
    public RecruiterProfileResponse createOrUpdateRecruiterProfile(RecruiterProfileRequest request) {
        User user = requireCurrentUserWithRole(Role.RECRUITER);
        RecruiterProfile profile = recruiterProfileRepository.findByUser(user)
                .orElseGet(() -> RecruiterProfile.builder().user(user).build());
        applyRecruiterChanges(profile, request);
        return toResponse(recruiterProfileRepository.save(profile));
    }

    @Transactional
    public RecruiterProfileResponse updateRecruiterProfile(Long profileId, RecruiterProfileRequest request) {
        User user = requireCurrentUserWithRole(Role.RECRUITER);
        RecruiterProfile profile = recruiterProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter profile not found"));
        requireOwner(profile.getUser(), user, "You can only update your own profile");
        applyRecruiterChanges(profile, request);
        return toResponse(recruiterProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyRecruiterProfile() {
        User user = requireCurrentUserWithRole(Role.RECRUITER);
        return toResponse(recruiterProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter profile not found")));
    }

    private void applyJobSeekerChanges(JobSeekerProfile profile, JobSeekerProfileRequest request) {
        requireText(request.getPhone(), "Phone number is required");
        profile.setPhone(request.getPhone());
        profile.setCity(request.getCity());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setSkills(request.getSkills());
        profile.setSummary(request.getSummary());
        if (request.getProfileComplete() != null) {
            profile.setProfileComplete(request.getProfileComplete());
        }
    }

    private void applyRecruiterChanges(RecruiterProfile profile, RecruiterProfileRequest request) {
        requireText(request.getCompanyName(), "Company name is required");
        profile.setCompanyName(request.getCompanyName());
        profile.setCompanyWebsite(request.getCompanyWebsite());
        profile.setLocation(request.getLocation());
        profile.setCompanyDescription(request.getCompanyDescription());
    }

    private User requireCurrentUserWithRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This operation requires the " + role + " role");
        }
        return user;
    }

    private void requireOwner(User owner, User currentUser, String message) {
        if (!owner.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private JobSeekerProfileResponse toResponse(JobSeekerProfile profile) {
        return JobSeekerProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .phone(profile.getPhone())
                .city(profile.getCity())
                .experienceLevel(profile.getExperienceLevel())
                .skills(profile.getSkills())
                .summary(profile.getSummary())
                .profileComplete(profile.isProfileComplete())
                .build();
    }

    private RecruiterProfileResponse toResponse(RecruiterProfile profile) {
        return RecruiterProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .companyName(profile.getCompanyName())
                .companyWebsite(profile.getCompanyWebsite())
                .location(profile.getLocation())
                .companyDescription(profile.getCompanyDescription())
                .build();
    }
}
