package com.fabricaescuela.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip JWT filter for Swagger and public endpoints
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Excluir Swagger, health check y todos los métodos GET en /api/**
        if (path != null && (path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.startsWith("/configuration") ||
            path.startsWith("/actuator/health") ||
            (method.equals("GET") && path.startsWith("/api/")))) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 🔒 Detectar si es una ruta protegida (POST/PUT/DELETE en /api/**)
        boolean isProtectedRoute = path != null && path.startsWith("/api/") && 
                                   (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"));

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("❌ Error al extraer username del token: " + e.getMessage());
                // Si es ruta protegida y el token es inválido, responder 401
                if (isProtectedRoute) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token JWT inválido o expirado\"}");
                    return;
                }
            }
        } else if (isProtectedRoute) {
            // ❌ Ruta protegida SIN token JWT → responder 401
            logger.warn("⚠️ Intento de acceso a ruta protegida sin token: {} {}", method, path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Se requiere autenticación. Token JWT no proporcionado.\"}");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                String role = jwtUtil.extractRole(jwt);
                List<String> permisos = jwtUtil.extractPermisos(jwt);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (permisos != null) {
                    authorities = permisos.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("✅ Usuario autenticado: {} con rol: {}", username, role);
            } else if (isProtectedRoute) {
                // ❌ Token inválido en ruta protegida
                logger.error("❌ Token JWT inválido para usuario: {}", username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token JWT inválido o expirado\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
