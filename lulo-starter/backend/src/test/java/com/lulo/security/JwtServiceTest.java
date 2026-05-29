package com.lulo.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-with-at-least-32-characters-please-pad";
    private final JwtService jwt = new JwtService(SECRET, 60_000);

    @Test
    void generaTokenConClaimsEsperados() {
        UUID userId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        String token = jwt.generate(userId, "u@x.com", empresaId,
                "ADMIN_EMPRESA", List.of("PROCESO_VER", "USUARIO_VER"));

        assertNotNull(token);
        assertFalse(token.isBlank());

        Claims claims = jwt.parse(token);
        assertEquals(userId.toString(), claims.getSubject());
        assertEquals("u@x.com", claims.get("email", String.class));
        assertEquals(empresaId.toString(), claims.get("empresaId", String.class));
        assertEquals("ADMIN_EMPRESA", claims.get("tipoUsuario", String.class));
    }

    @Test
    void superadminPuedeEmitirseSinEmpresaId() {
        UUID userId = UUID.randomUUID();
        String token = jwt.generate(userId, "admin@lulo.app", null,
                "SUPERADMIN", List.of("*"));

        Claims claims = jwt.parse(token);
        assertNull(claims.get("empresaId", String.class));
        assertEquals("SUPERADMIN", claims.get("tipoUsuario", String.class));
    }

    @Test
    void tokenConFirmaInvalidaFalla() {
        String token = jwt.generate(UUID.randomUUID(), "x@x.com",
                UUID.randomUUID(), "USUARIO", List.of());
        // Cambiar el último carácter rompe la firma.
        String mangled = token.substring(0, token.length() - 1) +
                (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwt.parse(mangled));
    }

    @Test
    void rechazaSecretCorto() {
        assertThrows(IllegalStateException.class,
                () -> new JwtService("too-short", 60_000));
    }
}
