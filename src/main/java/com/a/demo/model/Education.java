package com.a.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Education {

    @Id
    private Long id;
    private String degree;
    private String institution;
    private String startDate;
    private String endDate;
    private String description;
}
