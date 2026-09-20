package com.company.jobportal.repository.job;

import com.company.jobportal.entity.job.EmploymentType;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.user.RecruiterProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
        SELECT j FROM Job j
        WHERE (:keyword IS NULL OR :keyword = '' OR
           LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:location IS NULL OR :location = '' OR
           LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:employmentType IS NULL OR j.employmentType = :employmentType)
        """)
    Page<Job> search(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("employmentType") EmploymentType employmentType,
        Pageable pageable
    );

    Page<Job> findByRecruiterProfile(RecruiterProfile recruiterProfile, Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title,
            String requiredSkills,
            String location,
            Pageable pageable
    );

        Page<Job> findByEmploymentTypeAndTitleContainingIgnoreCaseOrEmploymentTypeAndRequiredSkillsContainingIgnoreCaseOrEmploymentTypeAndLocationContainingIgnoreCase(
            EmploymentType firstEmploymentType,
            String title,
            EmploymentType secondEmploymentType,
            String requiredSkills,
            EmploymentType thirdEmploymentType,
            String location,
            Pageable pageable
        );

    Page<Job> findByEmploymentType(EmploymentType employmentType, Pageable pageable);
}
