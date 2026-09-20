package com.company.jobportal.dto.resume;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class ResumeResponse {
    Long id;
    Long jobSeekerProfileId;
    String fileName;
    String filePath;
    String contentType;
    LocalDateTime uploadedAt;
}
