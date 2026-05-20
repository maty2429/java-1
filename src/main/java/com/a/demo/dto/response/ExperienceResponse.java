package com.a.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de salida (Response) para Experience.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceResponse {
    private Long id;
    private String jobTitle;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
