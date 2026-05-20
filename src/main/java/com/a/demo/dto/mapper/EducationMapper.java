package com.a.demo.dto.mapper;

import com.a.demo.dto.request.EducationRequest;
import com.a.demo.dto.response.EducationResponse;
import com.a.demo.model.Education;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper Education <-> DTOs.
 */
@Component
public class EducationMapper {

    public Education toEntity(EducationRequest request) {
        if (request == null) return null;
        Education e = new Education();
        e.setDegree(request.getDegree());
        e.setInstitution(request.getInstitution());
        e.setStartDate(request.getStartDate());
        e.setEndDate(request.getEndDate());
        e.setDescription(request.getDescription());
        return e;
    }

    public EducationResponse toResponse(Education e) {
        if (e == null) return null;
        return EducationResponse.builder()
                .id(e.getId())
                .degree(e.getDegree())
                .institution(e.getInstitution())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .description(e.getDescription())
                .build();
    }

    public List<EducationResponse> toResponseList(List<Education> list) {
        return list.stream().map(this::toResponse).toList();
    }
}
