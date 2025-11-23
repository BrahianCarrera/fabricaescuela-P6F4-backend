package com.fabricaescuela.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // permite todas las rutas
                        .allowedOriginPatterns("*") // usa patterns en lugar de origins con credentials
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // métodos HTTP
                        .allowedHeaders("*") // todos los headers
                        .allowCredentials(true) // permite credenciales
                        .maxAge(3600); // tiempo de caché de preflight
            }
        };
    }
}

