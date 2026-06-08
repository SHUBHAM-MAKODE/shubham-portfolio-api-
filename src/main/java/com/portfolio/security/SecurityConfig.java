package com.portfolio.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 🌟 Activates flexible method-level protection fallback if you ever need it
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${portfolio.frontend.url}")
    private String frontendUrl;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. Allow browser preflight handshake checks wide open access
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 🌟 2. PUBLIC USER SIDE INTERCEPTOR: Single Bootstrap Hydration Entry Point
                .requestMatchers("/api/public/portfolio-hub").permitAll()
                
                // 3. Public visitor standalone endpoint targets
                .requestMatchers("/api/messages/send", "/api/messages/submit").permitAll() 
                .requestMatchers("/api/trial/getAll").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/skills/all", "/api/specializations/all",
                                "/api/projects/all", "/api/services/all", "/api/resumes/all", 
                                "/api/education/all", "/api/experiences/all", "/api/resumes/active").permitAll()
                
                // 4. Authentication and baseline profiles access paths
                .requestMatchers("/api/admin/setup", "/api/admin/login", "/api/admin/profile").permitAll() 
                
                // ==========================================================================
                // 🔒 CENTRALIZED SECURITY VECTORS: Role-Based Access Control (RBAC)
                // ==========================================================================
                
                // 👁️ Both the Master Admin and Guest Viewer can hit administrative read mappings
                .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyRole("SUPER_ADMIN", "GUEST_VIEWER")
                
                // ❌ EXCLUSIVE MUTATION PROTECTION: Restrict ALL modifications strictly to your master profile
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("SUPER_ADMIN")
                
                // Catch-all safety boundary lock
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(List.of("http://localhost:5173",frontendUrl,"https://*.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}