package com.lulo.security;

import com.lulo.common.exception.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextTest {

    @AfterEach
    void clean() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String tipo, UUID empresaId) {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "x@y.com", empresaId, tipo, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, "tk", List.of()));
    }

    @Test
    void requireFallaSiNoHayAuth() {
        ApiException ex = assertThrows(ApiException.class, AuthContext::require);
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void requireSuperadminFallaParaUsuarioRegular() {
        loginAs("USUARIO", UUID.randomUUID());
        ApiException ex = assertThrows(ApiException.class, AuthContext::requireSuperadmin);
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void requireSuperadminPasaParaSuperadmin() {
        loginAs("SUPERADMIN", null);
        assertDoesNotThrow(AuthContext::requireSuperadmin);
    }

    @Test
    void requireAdminOSuperadminPasaParaAmbos() {
        loginAs("ADMIN_EMPRESA", UUID.randomUUID());
        assertDoesNotThrow(AuthContext::requireAdminEmpresaOSuperadmin);

        loginAs("SUPERADMIN", null);
        assertDoesNotThrow(AuthContext::requireAdminEmpresaOSuperadmin);
    }

    @Test
    void requireAdminFallaParaUsuarioRegular() {
        loginAs("USUARIO", UUID.randomUUID());
        ApiException ex = assertThrows(ApiException.class,
                AuthContext::requireAdminEmpresaOSuperadmin);
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }
}
