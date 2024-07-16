package com.oops.cablink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/test").permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(login -> login.successHandler(
                (request, response, authentication) -> {
                    response.sendRedirect("http://localhost:8000/api/user/me");
                }))
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .build();

    }
}
