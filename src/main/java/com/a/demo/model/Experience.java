package com.a.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Experience {

    @Id
    private Long id;
    private String jobTitle;
    private String companyName;
    private String startDate;
    private String endDate;
    private String description;
}
