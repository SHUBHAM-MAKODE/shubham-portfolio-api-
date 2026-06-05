package com.portfolio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Collections; // 🌟 Back backup structure tracking

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Airtight check for authenticating requests
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Log token errors silently so it doesn't break public routes
                logger.warn("JWT validation failed: " + e.getMessage());
            }
        }

        // Apply authentication if token is clean
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                
                List<SimpleGrantedAuthority> authorities;
                try {
                    // 🌟 SAFE EXECUTION ENGINE: Extract the role from the valid token
                    String role = jwtUtil.extractRole(jwt); 
                    
                    if (role != null) {
                        authorities = List.of(new SimpleGrantedAuthority(role));
                    } else {
                        authorities = Collections.emptyList();
                    }
                } catch (Exception e) {
                    logger.warn("Could not parse roles claim from token, defaulting to empty authorities.");
                    authorities = Collections.emptyList();
                }

                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // CRITICAL: Make sure this line is outside all if-statements so requests ALWAYS continue forward
        filterChain.doFilter(request, response);
    }
}