package com.events.eventservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // On désactive le CSRF car l'API est STATELESS et utilise des tokens JWT (pas
                                // de cookies de session)
                                // Cela évite les attaques CSRF car le token ne peut pas être volé via le
                                // navigateur automatiquement
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(authz -> authz
                                                // ATTENTION:permitAll() sur tout est utile pour le dev, mais doit être
                                                // restreint en PROD
                                                .requestMatchers("/actuator/**").permitAll()
                                                .anyRequest().permitAll())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}