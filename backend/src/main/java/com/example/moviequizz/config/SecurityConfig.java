package com.example.moviequizz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests()
                .requestMatchers("/api/movies/import-top").authenticated() // protect import endpoint
                .anyRequest().permitAll() // everything else is public
                .and()
                .httpBasic(); // use HTTP basic auth

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // For demo purposes only; in production use BCryptPasswordEncoder
        return NoOpPasswordEncoder.getInstance();
    }
}
