package com.example.cablink.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(login -> login.successHandler(
                (request, response, authentication) -> {
                    response.sendRedirect("/details");
                })).oauth2Client(Customizer.withDefaults()).logout(logout -> logout.logoutSuccessUrl("/").permitAll()).csrf(
                        AbstractHttpConfigurer::disable)
//                .logout(logout -> logout.logoutSuccessUrl("/").permitAll()).csrf(
//                        AbstractHttpConfigurer::disable) // TODO: Remove this line in production
//                .exceptionHandling(exceptionHandling -> exceptionHandling.defaultAuthenticationEntryPointFor(
//                        (request, response, accessDeniedException) -> {
//                            response.setStatus(401);
//                        },
//                        new RequestHeaderRequestMatcher("X-Requested-With", "BITSBids-Frontend")
//                ))
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
                .build();

    }
}
