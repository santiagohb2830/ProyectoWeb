package com.lulo.security;

import com.lulo.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthContext {

    private AuthContext() {}

    public static Optional<AuthenticatedUser> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser u)) {
            return Optional.empty();
        }
        return Optional.of(u);
    }

    public static AuthenticatedUser require() {
        return current().orElseThrow(() ->
                new ApiException("No autenticado", HttpStatus.UNAUTHORIZED));
    }

    public static AuthenticatedUser requireSuperadmin() {
        AuthenticatedUser u = require();
        if (!u.isSuperadmin()) {
            throw new ApiException("Requiere rol SUPERADMIN", HttpStatus.FORBIDDEN);
        }
        return u;
    }

    public static AuthenticatedUser requireAdminEmpresaOSuperadmin() {
        AuthenticatedUser u = require();
        if (!u.isSuperadmin() && !u.isAdminEmpresa()) {
            throw new ApiException("Requiere rol ADMIN_EMPRESA o SUPERADMIN", HttpStatus.FORBIDDEN);
        }
        return u;
    }
}
