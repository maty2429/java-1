package com.a.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String profileDescription;
    private String profileImageUrl;
    private Integer yearsOfExperience;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String githubUrl;
    private Set<Skill> skills;
    private Set<Education> educations;
    private Set<Experience> experiences;
}
