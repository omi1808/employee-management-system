package com.om.ems.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource; // ✅ Inject the CORS config bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // ✅ Enable CORS with your config
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                	    .requestMatchers("/api/auth/**").permitAll()  // Allow auth URLs
                	    .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMIN role
                	    .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER") // ADMIN and MANAGER
                	    .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE") // All roles for employees
                	    .requestMatchers("/api/attendance/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                	    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
