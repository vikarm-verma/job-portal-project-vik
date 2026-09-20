package com.company.jobportal.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recruiter_profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String companyName;

    @Size(max = 255)
    @Column(length = 255)
    private String companyWebsite;

    @Size(max = 100)
    @Column(length = 100)
    private String location;

    @Lob
    @Column
    private String companyDescription;
}
