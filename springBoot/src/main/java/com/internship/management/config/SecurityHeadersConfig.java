package com.internship.management.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Configuration des headers de sécurité HTTP
 */
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
                    @org.springframework.lang.NonNull HttpServletResponse response,
                    @org.springframework.lang.NonNull FilterChain filterChain)
                    throws ServletException, IOException {

                // Strict-Transport-Security (HSTS)
                // Force HTTPS pour 1 an
                response.setHeader("Strict-Transport-Security",
                        "max-age=31536000; includeSubDomains");

                // X-Content-Type-Options
                // Empêche le navigateur de deviner le type MIME
                response.setHeader("X-Content-Type-Options", "nosniff");

                // X-Frame-Options
                // Empêche le clickjacking
                response.setHeader("X-Frame-Options", "DENY");

                // X-XSS-Protection
                // Active la protection XSS du navigateur
                response.setHeader("X-XSS-Protection", "1; mode=block");

                // Referrer-Policy
                // Contrôle les informations de référence envoyées
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // Content-Security-Policy
                // Définit les sources de contenu autorisées
                response.setHeader("Content-Security-Policy",
                        "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data: https:; " +
                                "font-src 'self' data:; " +
                                "connect-src 'self' ws: wss:; " +
                                "frame-ancestors 'none'");

                // Permissions-Policy (anciennement Feature-Policy)
                // Contrôle les fonctionnalités du navigateur
                response.setHeader("Permissions-Policy",
                        "geolocation=(), microphone=(), camera=()");

                filterChain.doFilter(request, response);
            }
        };
    }
}
