package com.company.jobportal.entity.resume;

import com.company.jobportal.entity.user.JobSeekerProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "job_seeker_profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_seeker_profile_id", nullable = false, unique = true)
    private JobSeekerProfile jobSeekerProfile;

    @NotBlank(message = "Resume file name is required")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String fileName;

    @NotBlank(message = "Resume file path is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String filePath;

    @NotBlank(message = "Resume content type is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
