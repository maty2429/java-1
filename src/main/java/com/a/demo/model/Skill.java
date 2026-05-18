package com.a.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {

    @Id
    private Long id;
    private String name;
    private Integer levelPercentage;
    private String iconClass;
}
