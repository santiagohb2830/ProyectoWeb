package com.lulo.config;

import com.lulo.security.JwtAuthenticationFilter;
import com.lulo.security.TenantFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final TenantFilter tenantFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, TenantFilter tenantFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.tenantFilter = tenantFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/error").permitAll()
                // Refresh requiere token válido (lo valida el filtro JWT)
                .requestMatchers("/api/auth/refresh", "/api/auth/me").authenticated()
                // Superadmin o usuarios con permisos EMPRESA_* (granularidad)
                .requestMatchers("/api/superadmin/**")
                    .access((authSupplier, ctx) -> {
                        var a = authSupplier.get();
                        boolean ok = a != null && a.isAuthenticated() && a.getAuthorities().stream()
                                .anyMatch(g -> {
                                    String n = g.getAuthority();
                                    return n.equals("ROLE_SUPERADMIN")
                                            || n.startsWith("PERM_EMPRESA_")
                                            || n.equals("PERM_METRICAS_VER")
                                            || n.equals("PERM_AUDIT_GLOBAL_VER")
                                            || n.equals("PERM_LULO_USUARIO_VER")
                                            || n.equals("PERM_LULO_ROL_GESTIONAR")
                                            || n.equals("PERM_SOPORTE_PROCESOS_VER");
                                });
                        return new org.springframework.security.authorization.AuthorizationDecision(ok);
                    })
                // Métricas: solo superadmin
                .requestMatchers("/actuator/**").hasRole("SUPERADMIN")
                // Resto autenticado
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(tenantFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
