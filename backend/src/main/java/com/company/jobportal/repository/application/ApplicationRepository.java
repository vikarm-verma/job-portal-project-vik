package com.company.jobportal.repository.application;

import com.company.jobportal.entity.application.Application;
import com.company.jobportal.entity.application.ApplicationStatus;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.user.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJob(Job job);

    List<Application> findByJobSeekerProfile(JobSeekerProfile jobSeekerProfile);

    boolean existsByJobAndJobSeekerProfile(Job job, JobSeekerProfile jobSeekerProfile);

    List<Application> findByStatus(ApplicationStatus status);
}
