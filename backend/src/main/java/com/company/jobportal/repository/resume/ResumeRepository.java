package com.company.jobportal.repository.resume;

import com.company.jobportal.entity.resume.Resume;
import com.company.jobportal.entity.user.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByJobSeekerProfile(JobSeekerProfile jobSeekerProfile);
}
