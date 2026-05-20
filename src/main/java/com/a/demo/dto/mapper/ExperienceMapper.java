package com.a.demo.dto.mapper;

import com.a.demo.dto.request.ExperienceRequest;
import com.a.demo.dto.response.ExperienceResponse;
import com.a.demo.model.Experience;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper Experience <-> DTOs.
 */
@Component
public class ExperienceMapper {

    public Experience toEntity(ExperienceRequest request) {
        if (request == null) return null;
        Experience ex = new Experience();
        ex.setJobTitle(request.getJobTitle());
        ex.setCompanyName(request.getCompanyName());
        ex.setStartDate(request.getStartDate());
        ex.setEndDate(request.getEndDate());
        ex.setDescription(request.getDescription());
        return ex;
    }

    public ExperienceResponse toResponse(Experience ex) {
        if (ex == null) return null;
        return ExperienceResponse.builder()
                .id(ex.getId())
                .jobTitle(ex.getJobTitle())
                .companyName(ex.getCompanyName())
                .startDate(ex.getStartDate())
                .endDate(ex.getEndDate())
                .description(ex.getDescription())
                .build();
    }

    public List<ExperienceResponse> toResponseList(List<Experience> list) {
        return list.stream().map(this::toResponse).toList();
    }
}
