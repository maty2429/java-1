package com.a.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfing {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //el SecurityFilterChain es un filtro de seguridad.
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
        return http.build();
    }



    public UserDetailService userDetailService(){
        
    }










}
