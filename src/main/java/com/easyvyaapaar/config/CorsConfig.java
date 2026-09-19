package com.easyvyaapaar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Universal CORS configuration supporting wildcards and cross-origin Netlify requests.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOriginsStr;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        if (allowedOriginsStr != null && !allowedOriginsStr.trim().equals("*")) {
            List<String> origins = Arrays.stream(allowedOriginsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            config.setAllowedOrigins(origins);
        } else {
            // Allows wildcard domains (*, netlify.app, localhost) without throwing IllegalArgumentException
            config.addAllowedOriginPattern("*");
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
