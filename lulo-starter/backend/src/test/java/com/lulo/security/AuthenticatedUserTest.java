package com.lulo.security;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserTest {

    @Test
    void detectaSuperadminYBypasaEmpresa() {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "admin@lulo.app", null,
                "SUPERADMIN", List.of("PROCESO_VER"));
        assertTrue(u.isSuperadmin());
        assertFalse(u.isAdminEmpresa());
        assertTrue(u.hasPermiso("PROCESO_VER"));
        assertFalse(u.hasPermiso("OTRO"));
    }

    @Test
    void adminEmpresaNoEsSuperadmin() {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "a@e.com", UUID.randomUUID(),
                "ADMIN_EMPRESA", List.of());
        assertFalse(u.isSuperadmin());
        assertTrue(u.isAdminEmpresa());
    }

    @Test
    void usuarioRegularNiAdminNiSuper() {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "u@e.com", UUID.randomUUID(),
                "USUARIO", List.of());
        assertFalse(u.isSuperadmin());
        assertFalse(u.isAdminEmpresa());
    }
}
