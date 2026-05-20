package com.a.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de salida (Response) para Education.
 * LocalDate se serializa a "YYYY-MM-DD" automaticamente por Jackson.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationResponse {
    private Long id;
    private String degree;
    private String institution;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
