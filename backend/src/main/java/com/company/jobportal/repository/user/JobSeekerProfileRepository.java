package com.company.jobportal.repository.user;

import com.company.jobportal.entity.user.JobSeekerProfile;
import com.company.jobportal.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {

    Optional<JobSeekerProfile> findByUser(User user);
}
