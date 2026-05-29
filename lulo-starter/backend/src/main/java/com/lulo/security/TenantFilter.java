package com.lulo.security;

import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Activa el Hibernate Filter "empresaFilter" con el empresaId del usuario
 * autenticado. SUPERADMIN no activa el filtro (ve todas las empresas).
 *
 * Corre después del JwtAuthenticationFilter (orden +10).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TenantFilter extends OncePerRequestFilter {

    private final EntityManager entityManager;

    public TenantFilter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Session session = null;
        try {
            session = entityManager.unwrap(Session.class);
        } catch (IllegalStateException ignored) {
            // EntityManager sin sesión activa: ignorar, JPA la abrirá perezosamente.
        }
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser user) {
            if (!user.isSuperadmin() && user.empresaId() != null && session != null) {
                session.enableFilter("empresaFilter")
                        .setParameter("empresaId", user.empresaId());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            if (session != null) {
                try { session.disableFilter("empresaFilter"); } catch (Exception ignored) {}
            }
        }
    }
}
