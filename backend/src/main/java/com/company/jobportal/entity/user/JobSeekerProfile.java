package com.company.jobportal.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_seeker_profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String phone;

    @Size(max = 100)
    @Column(length = 100)
    private String city;

    @Size(max = 100)
    @Column(length = 100)
    private String experienceLevel;

    @Size(max = 200)
    @Column(length = 200)
    private String skills;

    @Lob
    @Column
    private String summary;

    @NotNull(message = "Profile completion status is required")
    @Column(nullable = false)
    private boolean profileComplete = false;
}
